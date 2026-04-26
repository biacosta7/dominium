package com.dominium.backend.domain.assembleia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AssembleiaId implements Serializable {
    private String id;

    public static AssembleiaId gerar() {
        return new AssembleiaId(UUID.randomUUID().toString());
    }
}