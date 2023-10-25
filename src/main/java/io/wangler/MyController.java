package io.wangler;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/views")
public class MyController {
    
    @Get("/pebble2")
    public ModelAndView<Map<String, Object>> pebble2() {
        Map<String, Object> map = new HashMap<>();
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        map.put("numbers", numbers);
        return new ModelAndView<>("pebble/home2", map);
    }
}