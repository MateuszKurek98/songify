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
    Map<Integer, Song> database = new HashMap<>(Map.of(
            1, new Song("In The End", "Linkin Park"),
            2, new Song("Hate you", "Three Days Grace"),
            3, new Song("Papercut", "Linkin Park"),
            4, new Song("Smallow","Linkin Park")
    ));
    @GetMapping("/songs")
    public ResponseEntity<SongResponseDto> getAllSongs(@RequestParam(required = false) Integer limit){
        if(limit != null){
            Map<Integer, Song> limitedMap = database.entrySet()
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
        Song song = database.get(id);
        SingleSongResponseDto response = new SingleSongResponseDto(song);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/songs")
    public ResponseEntity<SingleSongResponseDto> postSong(@RequestBody @Valid SongRequestDto request){
        Song song = new Song(request.songName(), request.artist());
        log.info("added new Song: " + song);
        database.put(database.size() + 1, song);
        return ResponseEntity.ok(new SingleSongResponseDto(song));
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
    public ResponseEntity<UpdateSongResponseDto> update(@PathVariable Integer id,
                                                        @RequestBody @Valid UpdateSongRequestDto request) {
        if (!database.containsKey(id)) {
            throw new SongNotFoundException("Song with id " + id + " not found");
        }
        String newSongName = request.songName();
        String newArtist = request.artist();
        Song newSong = new Song(newSongName, newArtist);
        Song oldSong = database.put(id, newSong);
        log.info("Update song with id: " + id +
                " with oldSongName: " + oldSong.name() +
                " to new song: " + newSong.name() +
                " old artist: " + oldSong.artist() +
                " to new artist: " + newSong.artist());
        return ResponseEntity.ok(new UpdateSongResponseDto(newSong.name(), newSong.artist()));
    }
}
