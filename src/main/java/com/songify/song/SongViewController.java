package com.songify.song;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SongViewController {
    private Map<Integer, String> database = new HashMap<>();
    @GetMapping("/")
    public String home(){
        return "home";
    }
    @GetMapping("/view/songs")
    public String songs(Model model){
        database.put(1, "shawnmendes song1");
        database.put(2, "ariana grande song2");
        database.put(3, "trzecia piosenka");
        database.put(4, "czwarta piosenka");

        model.addAttribute("songMap", database);
        return "songs";
        //11 min - html
    }
}
