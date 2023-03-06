package com.tjandra.dspfileprocessor.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ProcessImagesRequestDTO {

    @JsonProperty("folder_path")
    private String folderPath;
}
