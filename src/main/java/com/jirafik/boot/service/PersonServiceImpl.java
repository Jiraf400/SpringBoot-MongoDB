package com.jirafik.boot.service;

import com.jirafik.boot.collection.Person;
import com.jirafik.boot.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.ROOT;
import static org.springframework.data.support.PageableExecutionUtils.getPage;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Person save(Person person) {
        if (person != null) return repository.save(person);
        else throw new RuntimeException("Entity must be not null.");
    }

    @Override
    public List<Person> getPersonByName(String name) {

        List<Person> personList1 = new ArrayList<>();
        for (Person p : repository.findAll()) {
            if (p.getUsername().equalsIgnoreCase(name)) personList1.add(p);
        }
        return personList1;
    }


    @Override
    public String delete(String id) {
        repository.deleteById(id);
        return "Entity with id = [" + id + "] was deleted.";
    }

    @Override
    public List<Person> getByPersonAge(Integer minAge, Integer maxAge) {
        return repository.findPersonByAgeBetween(minAge, maxAge);
    }

    @Override
    public Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable) {

        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        if (name != null && !name.isEmpty()) {            //contains name ignore cases
            criteria.add(Criteria.where("username").regex(name, "i"));
        }

        if (minAge != null && maxAge != null) {
            criteria.add(Criteria.where("age").gte(minAge).lte(maxAge));
        }

        if (city != null && !city.isEmpty()) {
            criteria.add(Criteria.where("addresses.city").is(city));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria()
                    .andOperator(criteria.toArray(new Criteria[0])));
        }

        return getPage(mongoTemplate.find(query, Person.class),
                pageable, () -> mongoTemplate.count(query.skip(0).limit(0), Person.class));
    }

    @Override
    public List<Document> getOldestPersonByCity(String city) {

        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");      //unwrap addresses list
        SortOperation sortOperation
                = Aggregation.sort(DESC, "age");    //sort persons by age
        GroupOperation groupOperation
                = Aggregation.group("addresses.city")   //group cities
                .first(ROOT)
                .as("oldestPerson");    //store as oldestPerson

        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation, sortOperation, groupOperation);

        // List<Document> person
        return mongoTemplate.aggregate(aggregation, Person.class, Document.class).getMappedResults();
    }

    @Override
    public List<Document> getPopulationByCity() {

        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");
        GroupOperation groupOperation
                = Aggregation.group("addresses.city")
                .count().as("popCount");
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC, "popCount");

        ProjectionOperation projectionOperation
                = Aggregation.project()
                .andExpression("_id").as("city")
                .andExpression("popCount").as("count")
                .andExclude("_id");

        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation,groupOperation,sortOperation,projectionOperation);

                //List<Document>;;
        return  mongoTemplate.aggregate(aggregation,
                Person.class,
                Document.class).getMappedResults();
    }
}
