package com.example.lab2.specification;

import com.example.lab2.entities.Student;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {

    public static Specification<Student> hasFirstName(String name) {
        return (root, query, builder) ->
                name == null ? null : builder.equal(builder.lower(root.get("firstName")), name.toLowerCase());
    }

    public static Specification<Student> hasFirstNameLike(String nameLike) {
        return (root, query, builder) ->
                nameLike == null ? null : builder.like(builder.lower(root.get("firstName")), "%" + nameLike.toLowerCase() + "%");
    }

    public static Specification<Student> hasLastName(String lastName) {
        return (root, query, builder) ->
                lastName == null ? null : builder.equal(builder.lower(root.get("lastName")), lastName.toLowerCase());
    }

    public static Specification<Student> hasLastNameLike(String lastNameLike) {
        return (root, query, builder) ->
                lastNameLike == null ? null : builder.like(builder.lower(root.get("lastName")), "%" + lastNameLike.toLowerCase() + "%");
    }

    public static Specification<Student> hasEmail(String email) {
        return (root, query, builder) ->
                email == null ? null : builder.equal(builder.lower(root.get("email")), email.toLowerCase());
    }

    public static Specification<Student> hasGroup(String group) {
        return (root, query, builder) ->
                group == null ? null : builder.equal(root.get("groupName"), group);
    }
}
