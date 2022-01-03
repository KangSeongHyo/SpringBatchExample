package com.example.spbh;


import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Getter
@Entity
@NamedQuery(name = "input",query = "SELECT i FROM Input i")
@ToString
public class Input {

    @Id
    @GeneratedValue
    private Long id;

    private String value;

    private String type;
}
