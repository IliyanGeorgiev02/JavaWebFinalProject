package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.CreateListDto;
import WebProject.example.WebProject.softUni.dtos.FindListDto;
import WebProject.example.WebProject.softUni.model.Comment;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.repositories.CustomListRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
}

