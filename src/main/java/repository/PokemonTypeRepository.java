package repository;

import bo.PokemonType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PokemonTypeRepository {

    private List<PokemonType> pokemons;

    public PokemonTypeRepository() {
        try {
            var pokemonStream = this.getClass().getResourceAsStream("/pokemons.json");

            var objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
            var pokemonArray = objectMapper.readValue(pokemonStream, PokemonType[].class);
            this.pokemons = Arrays.asList(pokemonArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PokemonType findPokemonById(int id) {
        System.out.println("Loading Pokemon information for Pokemon id " + id);

        for(PokemonType p : pokemons) {
            if(p.getId() == id) {
                return p;
            }
        }

        return null;
    }

    public PokemonType findPokemonByName(String name) {
        System.out.println("Loading Pokemon information for Pokemon name " + name);

        for(PokemonType p : pokemons) {
            if(p.getName().equals(name)) {
                return p;
            }
        }

        return null;
    }

    public List<PokemonType> findAllPokemon() {
        return pokemons;
    }
}
