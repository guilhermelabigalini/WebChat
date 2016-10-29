/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author guilherme
 */
@RestController
// http://localhost:8080/swagger-ui.html
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(path = "/greeting", method = GET)
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    /**
     * http://localhost:8080/greeting
     * POST { "id": 3, "content": "Hello, World!" }
     *
     * @param g
     * @return
     */
    @RequestMapping(path = "/greeting", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Greeting postgreeting(@RequestBody Greeting g) {
        g.setId(counter.incrementAndGet());
        g.setContent(g.getContent() + " Added!");
        return g;
    }

    /**
     * PUT http://localhost:8080/greeting/999
     * { "content": "Hello, World!" }
     * @param id
     * @param g
     * @return 
     */
    @RequestMapping(path = "/greeting/{id}", method = PUT)
    public Greeting putgreeting(@PathVariable("id") int id, @RequestBody Greeting g) {
        g.setId(id);
        g.setContent(g.getContent() + " Updated!");
        return g;
    }
}
