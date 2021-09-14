package com.in28minutes.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.in28minutes.rest.webservices.restfulwebservices.exception.UserNotFoundException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class UserController {

    private UserDaoService userService;

    public UserController(UserDaoService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public EntityModel<User> getUser(@PathVariable Integer id) {
        User user =  userService.findUser(id);
        if(user == null) {
            throw new UserNotFoundException("id-" + id);
        }

        EntityModel<User> model = EntityModel.of(user);

        WebMvcLinkBuilder linkToUsers = linkTo(methodOn(this.getClass()).retrieveAllUsers());

        model.add(linkToUsers.withRel("all-users"));

        return model;
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
        User savedUser = userService.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Integer id) {
        User user =  userService.deletedById(id);
        if(user == null) {
            throw new UserNotFoundException("id-" + id);
        }
    }
}
