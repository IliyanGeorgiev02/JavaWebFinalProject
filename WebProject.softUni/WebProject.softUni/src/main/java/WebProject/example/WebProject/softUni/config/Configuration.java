package WebProject.example.WebProject.softUni.config;

import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.model.Movie;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<MovieFullInfoDto, Movie>() {
            @Override
            protected void configure() {
                skip().setId(null);
                map().setImdbId(source.getImdbId());
            }
        });
        return modelMapper;
    }

    @Bean

    public Gson gson() {
        return new Gson();
    }

    @Bean

    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
