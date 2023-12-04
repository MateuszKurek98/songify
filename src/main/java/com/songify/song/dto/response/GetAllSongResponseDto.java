package com.songify.song.dto.response;

import com.songify.song.controller.Song;

import java.util.Map;
public record GetAllSongResponseDto(Map<Integer, Song> songs) {

}
