package com.mitchtalmadge.cryptex.web.api.controllers;

import com.mitchtalmadge.cryptex.web.api.APIControllerAbstract;
import com.mitchtalmadge.cryptex.web.api.annotations.APIController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;


@APIController
public class ExampleController extends APIControllerAbstract {

    @RequestMapping("hello")
    public ResponseEntity<?> hello() throws Exception {
        throw new Exception("lmao");
    }

}
