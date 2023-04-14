package com.jirafik.boot.collection;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@Data
@Builder
@Document(collection = "person")
@JsonInclude(NON_NULL)
public class Person {

    @Id
    private String personId;
    private String username;
    private Integer age;
    private List<String> hobbies;
    private List<Address> addresses;
}
