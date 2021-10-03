package com.example.spbh;


import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "input",query = "SELECT i FROM Input i")
@ToString
public class Input {

    @Id
    @GeneratedValue
    Long id;

    String value;
}
