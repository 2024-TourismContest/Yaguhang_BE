package _4.TourismContest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TestController {

    @GetMapping("/test1")
    public ResponseEntity<String> getTestAPI() {
        String response = "CICD Test";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/test2")
    public ResponseEntity<String> getTestAPI2() {
        String response = "Final CICD";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}