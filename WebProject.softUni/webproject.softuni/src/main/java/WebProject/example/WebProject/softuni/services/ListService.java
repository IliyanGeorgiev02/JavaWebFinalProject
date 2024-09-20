package webproject.example.webproject.softuni.services;

import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.*;
import webproject.example.webproject.softuni.repositories.CustomListRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        mappedList.setLikes(new HashSet<>());
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

    public void likeList(Long listId, User user) {
        Optional<CustomList> byId = this.customListRepository.findById(listId);
        if (byId.isPresent()) {
            CustomList list = byId.get();
            boolean alreadyLiked = list.getLikes().stream()
                    .anyMatch(like -> like.getLiked().equals(user));
            if (!alreadyLiked) {
                Like like = new Like();
                like.setLiked(user);
                like.setCustomList(list);
                list.getLikes().add(like);
                this.customListRepository.save(list);
            }
        }
    }

    public void dislikeList(Long listId, User user) {
        Optional<CustomList> byId = this.customListRepository.findById(listId);
        if (byId.isPresent()) {
            CustomList list = byId.get();
            Optional<Like> likeToRemove = list.getLikes().stream()
                    .filter(like -> like.getLiked().equals(user))
                    .findFirst();
            if (likeToRemove.isPresent()) {
                list.getLikes().remove(likeToRemove.get());
                this.customListRepository.save(list);
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
        displayListDto.setLikes(customList.getLikes().size());
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

