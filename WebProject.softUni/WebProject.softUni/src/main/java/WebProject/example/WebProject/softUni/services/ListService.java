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

    public void addList(CreateListDto listDto) {
        CustomList mappedList = this.modelMapper.map(listDto, CustomList.class);
        mappedList.setLikes(0);
        User user = this.userHelperService.getUser();
        mappedList.setUser(user);
        customListRepository.saveAndFlush(mappedList);
    }

    public Optional<List<Movie>> findAllInMovieList(String title, String description) {
        return Optional.ofNullable(this.customListRepository.findMoviesByTitleAndDescription(title, description));
    }

    public List<CustomList> findAllLists() {
        return this.customListRepository.findAll();
    }

    public CustomList findListByTitleAndDescription(FindListDto findListDto) {
        return this.customListRepository.findByTitleAndDescription(findListDto.getTitle(),findListDto.getDescription());
    }

    public Optional<CustomList> findListById(Long id) {
        return this.customListRepository.findById(id);
    }

    public void deleteList(CustomList customList) {
        this.customListRepository.delete(customList);
    }
}
