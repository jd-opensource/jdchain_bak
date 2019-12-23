package com.jd.blockchain.ump.controller;

import com.jd.blockchain.ump.dao.DBConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/db/")
public class UmpDBController {

    @Autowired
    private DBConnection dbConnection;

    @RequestMapping(method = RequestMethod.GET, path = "read/{key}")
    public String read(@PathVariable(name = "key") String key) {

        return dbConnection.get(key);
    }
}
