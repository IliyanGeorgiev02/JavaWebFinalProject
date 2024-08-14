package webproject.example.webproject.softuni.services;

import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.model.Movie;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.repositories.CustomListRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ListService {

    private final ModelMapper modelMapper;
    private final CustomListRepository customListRepository;
    private final UserHelperService userHelperService;

    public ListService(ModelMapper modelMapper, CustomListRepository customListRepository, UserHelperService userHelperService) {
        this.modelMapper = modelMapper;
        this.customListRepository = customListRepository;
        this.userHelperService = userHelperService;
    }

    public CustomList addList(CreateListDto listDto) {
        CustomList mappedList = this.modelMapper.map(listDto, CustomList.class);
        mappedList.setLikes(0);
        User user = this.userHelperService.getUser();
        mappedList.setUser(user);
        mappedList.setCreated(LocalDate.now());
        customListRepository.saveAndFlush(mappedList);
        return mappedList;
    }

    public Optional<List<Movie>> findAllInMovieList(String title, String description) {
        return Optional.ofNullable(this.customListRepository.findMoviesByTitleAndDescription(title, description));
    }

    public List<CustomList> findAllLists() {
        return this.customListRepository.findAll();
    }

    public CustomList findListByTitleAndDescription(FindListDto findListDto) {
        return this.customListRepository.findByTitleAndDescription(findListDto.getTitle(), findListDto.getDescription());
    }

    public Optional<CustomList> findListById(Long id) {
        return this.customListRepository.findById(id);
    }

    public void deleteList(CustomList customList) {
        this.customListRepository.delete(customList);
    }

    public List<CustomList> findListByUsername(String username) {
        return this.customListRepository.findByUsername(username);
    }

    public void saveList(CustomList customList) {
        this.customListRepository.saveAndFlush(customList);
    }

    public void likeList(Long listId) {
        Optional<CustomList> byId = this.customListRepository.findById(listId);
        if (byId.isPresent()) {
            CustomList customList = byId.get();
            customList.setLikes(customList.getLikes() + 1);
            customListRepository.save(customList);
        }
    }

    public void dislikeList(Long listId) {
        Optional<CustomList> byId = this.customListRepository.findById(listId);
        if (byId.isPresent()) {
            CustomList customList = byId.get();
            int likes = customList.getLikes();
            if (likes - 1 >= 0) {
                customList.setLikes(customList.getLikes() - 1);
                customListRepository.save(customList);
            }
        }
    }

    public List<CustomList> findListsByUser(long id) {
        return this.customListRepository.findByUserId(id);
    }

    public ListDto mapCustomListsToListDto(List<CustomList> customLists) {
        List<DisplayListDto> displayListDtos = customLists.stream()
                .map(this::convertToDisplayListDto)
                .collect(Collectors.toList());
        ListDto listDto = new ListDto();
        listDto.setLists(displayListDtos);

        return listDto;
    }

    public DisplayListDto convertToDisplayListDto(CustomList customList) {
        DisplayListDto displayListDto = new DisplayListDto();
        displayListDto.setTitle(customList.getTitle());
        displayListDto.setDescription(customList.getDescription());
        displayListDto.setLikes(customList.getLikes());
        displayListDto.setId(customList.getId());
        List<DisplayMovieInListDto> movieDtos = customList.getMovies().stream()
                .map(this::convertToDisplayMovieInListDto)
                .collect(Collectors.toList());

        displayListDto.setMovies(movieDtos);

        return displayListDto;
    }

    public DisplayMovieInListDto convertToDisplayMovieInListDto(Movie movie) {
        DisplayMovieInListDto displayMovieInListDto = new DisplayMovieInListDto();
        displayMovieInListDto.setId(movie.getId());
        displayMovieInListDto.setPosterUrl(movie.getPosterUrl());
        displayMovieInListDto.setTitle(movie.getTitle());
        displayMovieInListDto.setYear(movie.getYear().toString());
        return displayMovieInListDto;
    }


}

