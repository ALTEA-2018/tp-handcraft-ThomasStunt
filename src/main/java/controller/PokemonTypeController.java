package controller;

import Annotations.Controller;
import Annotations.RequestMapping;
import bo.PokemonType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import repository.PokemonTypeRepository;

import java.util.Map;

@Controller
public class PokemonTypeController {
    private PokemonTypeRepository repository = new PokemonTypeRepository();

    @RequestMapping(uri="/pokemon")
    public PokemonType getPokemon(Map<String,String[]> parameters){

        if(parameters == null) {
            throw new IllegalArgumentException("parameters should not be empty");
        }

        PokemonType pt;

        if(parameters.containsKey("name")) {
            pt = repository.findPokemonByName(parameters.get("name")[0]);
        } else if(parameters.containsKey("id")) {
            pt = repository.findPokemonById(Integer.valueOf(parameters.get("id")[0]));
        } else {
            throw new IllegalArgumentException("unknown parameter");
        }

        return pt;
    }
}
