package com.songify.song.controller;

import com.songify.song.dto.request.PartiallyUpdateSongRequestDto;
import com.songify.song.dto.request.CreateSongRequestDto;
import com.songify.song.dto.request.UpdateSongRequestDto;
import com.songify.song.dto.response.*;
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
@RequestMapping("/songs")
public class SongRestController {
    Map<Integer, Song> database = new HashMap<>(Map.of(
            1, new Song("In The End", "Linkin Park"),
            2, new Song("Hate you", "Three Days Grace"),
            3, new Song("Papercut", "Linkin Park"),
            4, new Song("Smallow","Linkin Park")
    ));
    @GetMapping()
    public ResponseEntity<GetAllSongResponseDto> getAllSongs(@RequestParam(required = false) Integer limit){
        if(limit != null){
            Map<Integer, Song> limitedMap = database.entrySet()
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            GetAllSongResponseDto response = new GetAllSongResponseDto(limitedMap);
            return ResponseEntity.ok(response);
        }
        GetAllSongResponseDto response = new GetAllSongResponseDto(database);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<GetSongResponseDto> getSongById(@PathVariable Integer id,
                                                             @RequestHeader(required = false) String requestId){
        log.info(requestId);
        if(!database.containsKey(id)){
            throw new SongNotFoundException("Song with id " + id + " not found");
        }
        Song song = database.get(id);
        GetSongResponseDto response = new GetSongResponseDto(song);
        return ResponseEntity.ok(response);
    }
    @PostMapping()
    public ResponseEntity<CreateSongResponseDto> postSong(@RequestBody @Valid CreateSongRequestDto request){
        Song song = new Song(request.songName(), request.artist());
        log.info("added new Song: " + song);
        database.put(database.size() + 1, song);
        return ResponseEntity.ok(new CreateSongResponseDto(song));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteSongResponseDto> deleteSongByIdUsingPathVariable(@PathVariable  Integer id){
        if(!database.containsKey(id)){
            throw new SongNotFoundException("Song with id " + id + " not found");
        }
        database.remove(id);
        return ResponseEntity.ok(new DeleteSongResponseDto("You deleted song by id: " + id, HttpStatus.OK));
    }
    @PutMapping("/{id}")
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
    @PatchMapping("/{id}")
    public ResponseEntity<PartiallyUpdateSongResponseDto> partiallyUpdateSong(@PathVariable Integer id,
                                                                              @RequestBody PartiallyUpdateSongRequestDto request){
        if(!database.containsKey(id)){
            throw new RuntimeException("song with id: " + id + " not found");
        }
        Song songFromDatabase = database.get(id);
        Song.SongBuilder biulder = Song.builder();
        if(request.songName() != null){
            biulder.name(request.songName());
            log.info("Partially update song name");
        } else {
            biulder.name(songFromDatabase.name());
        }
        if(request.artist() != null){
            biulder.artist(request.artist());
            log.info("Partially update artist");
        } else {
            biulder.artist(songFromDatabase.artist());
        }
        Song updatedSong = biulder.build();
        database.put(id, updatedSong);
        return ResponseEntity.ok(new PartiallyUpdateSongResponseDto(updatedSong));
    }
}
