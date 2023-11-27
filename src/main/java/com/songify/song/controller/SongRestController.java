package com.songify.song.controller;

import com.songify.song.dto.request.SongRequestDto;
import com.songify.song.dto.request.UpdateSongRequestDto;
import com.songify.song.dto.response.DeleteSongResponseDto;
import com.songify.song.dto.response.SingleSongResponseDto;
import com.songify.song.dto.response.SongResponseDto;
import com.songify.song.dto.response.UpdateSongResponseDto;
import com.songify.song.error.SongNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class SongRestController {
    Map<Integer, String> database = new HashMap<>(Map.of(
            1, "shawnmendes song1",
            2, "ariana grande song2",
            3, "trzecia piosenka",
            4, "czwarta piosenka"
    ));
    @GetMapping("/songs")
    public ResponseEntity<SongResponseDto> getAllSongs(@RequestParam(required = false) Integer limit){
        if(limit != null){
            Map<Integer, String> limitedMap = database.entrySet()
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            SongResponseDto response = new SongResponseDto(limitedMap);
            return ResponseEntity.ok(response);
        }
        SongResponseDto response = new SongResponseDto(database);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/songs/{id}")
    public ResponseEntity<SingleSongResponseDto> getSongById(@PathVariable Integer id, @RequestHeader(required = false) String requestId){
        log.info(requestId);
        if(!database.containsKey(id)){
            throw new SongNotFoundException("Song with id " + id + " not found");
        }
        String song = database.get(id);
        SingleSongResponseDto response = new SingleSongResponseDto(song);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/songs")
    public ResponseEntity<SingleSongResponseDto> postSong(@RequestBody @Valid SongRequestDto request){
        String songName = request.songName();
        log.info("added new Song: " + songName);
        database.put(database.size() + 1, songName);
        return ResponseEntity.ok(new SingleSongResponseDto(songName));
    }
    @DeleteMapping("/song/{id}")
    public ResponseEntity<DeleteSongResponseDto> deleteSongByIdUsingPathVariable(@PathVariable  Integer id){
        if(!database.containsKey(id)){
            throw new SongNotFoundException("Song with id " + id + " not found");
        }
        database.remove(id);
        return ResponseEntity.ok(new DeleteSongResponseDto("You deleted song by id: " + id, HttpStatus.OK));
    }
    @PutMapping("/song/{id}")
    public ResponseEntity<UpdateSongResponseDto> update(@PathVariable Integer id, @RequestBody UpdateSongRequestDto request) {
        if (!database.containsKey(id)) {
            throw new SongNotFoundException("Song with id " + id + " not found");
        }
        String newSongName = request.songName();
        String oldSongName = database.put(id, newSongName);
        log.info("Update song with id: " + id + " with oldSongName: " + oldSongName + " to new song: " + newSongName);
        return ResponseEntity.ok(new UpdateSongResponseDto(newSongName));

    }
}
