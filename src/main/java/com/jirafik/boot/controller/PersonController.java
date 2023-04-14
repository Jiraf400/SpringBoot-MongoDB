package com.jirafik.boot.controller;

import com.jirafik.boot.collection.Person;
import com.jirafik.boot.service.PersonService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping
    public Person save(@RequestBody Person person) {
        return personService.save(person);
    }


    @GetMapping
    public List<Person> getPersonByName(@RequestParam("name") String name) {
        return personService.getPersonByName(name);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        return personService.delete(id);
    }


    @GetMapping("/age")
    public List<Person> getByAge(@RequestParam Integer minAge, @RequestParam Integer maxAge) {
        return personService.getByPersonAge(minAge, maxAge);
    }

    @GetMapping("/search")
    public Page<Person> searchPerson(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") Integer page,     //page number
            @RequestParam(defaultValue = "5") Integer size      //number of items on page
    ) {
        Pageable pageable
                = PageRequest.of(page,size);
        return personService.search(name,minAge,maxAge,city,pageable);
    }

    @GetMapping("/oldestPerson/{city}")
    public List<Document> getOldestPerson(@PathVariable String city) {
        return personService.getOldestPersonByCity(city);
    }

//    @GetMapping("/populationByCity")
//    public List<Document> getPopulationByCity() {
//        return personService.getPopulationByCity();
//    }
}
