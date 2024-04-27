package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Desserelializando pegue o json Title e guarde no atributo titulo e
// assim por diante.

@JsonIgnoreProperties(ignoreUnknown = true) // ignore todas as outras propriedades que não estejam no @JsonAlias
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao) {
}
