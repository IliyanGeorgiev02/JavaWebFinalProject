package webproject.example.webproject.softuni.services;

import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.model.Movie;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.repositories.CustomListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.Year;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ListServiceTest {
    @Mock
    private CustomListRepository customListRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserHelperService userHelperService;

    @InjectMocks
    private ListService listService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void likeList_whenListExists_thenLikesIncremented() {
        Long listId = 1L;
        CustomList customList = new CustomList();
        customList.setLikes(5);

        when(customListRepository.findById(listId)).thenReturn(Optional.of(customList));
        listService.likeList(listId);
        verify(customListRepository).findById(listId);
        verify(customListRepository).save(customList);
        assertEquals(6, customList.getLikes());
    }

    @Test
    void likeList_whenListDoesNotExist_thenNoActionTaken() {
        Long listId = 1L;
        when(customListRepository.findById(listId)).thenReturn(Optional.empty());
        listService.likeList(listId);
        verify(customListRepository).findById(listId);
        verify(customListRepository, never()).save(any(CustomList.class));
    }

    @Test
    void dislikeList_whenListExistsAndLikesGreaterThanZero_thenLikesDecremented() {
        Long listId = 1L;
        CustomList customList = new CustomList();
        customList.setLikes(5);

        when(customListRepository.findById(listId)).thenReturn(Optional.of(customList));
        listService.dislikeList(listId);
        verify(customListRepository).findById(listId);
        verify(customListRepository).save(customList);
        assertEquals(4, customList.getLikes());
    }

    @Test
    void dislikeList_whenListExistsAndLikesZero_thenNoActionTaken() {
        Long listId = 1L;
        CustomList customList = new CustomList();
        customList.setLikes(0);

        when(customListRepository.findById(listId)).thenReturn(Optional.of(customList));
        listService.dislikeList(listId);
        verify(customListRepository).findById(listId);
        verify(customListRepository, never()).save(any(CustomList.class));
        assertEquals(0, customList.getLikes());
    }

    @Test
    void dislikeList_whenListDoesNotExist_thenNoActionTaken() {
        Long listId = 1L;
        when(customListRepository.findById(listId)).thenReturn(Optional.empty());
        listService.dislikeList(listId);
        verify(customListRepository).findById(listId);
        verify(customListRepository, never()).save(any(CustomList.class));
    }

    @Test
    void addList_whenCalled_thenListIsCreatedAndSaved() {
        CreateListDto listDto = new CreateListDto();
        CustomList mappedList = new CustomList();
        User user = new User();
        when(modelMapper.map(listDto, CustomList.class)).thenReturn(mappedList);
        when(userHelperService.getUser()).thenReturn(user);
        CustomList result = listService.addList(listDto);
        verify(modelMapper).map(listDto, CustomList.class);
        verify(userHelperService).getUser();
        verify(customListRepository).saveAndFlush(mappedList);

        assertEquals(0, mappedList.getLikes(), "Likes should be initialized to 0");
        assertEquals(user, mappedList.getUser(), "User should be set from UserHelperService");
        assertEquals(mappedList, result, "The returned list should be the same as the saved list");
    }

    @Test
    void mapCustomListsToListDto_whenCalled_thenReturnsListDtoWithMappedValues() {
        CustomList customList1 = createCustomList(1L, "List 1", "Description 1", 10);
        CustomList customList2 = createCustomList(2L, "List 2", "Description 2", 20);

        List<CustomList> customLists = List.of(customList1, customList2);
        ListDto result = listService.mapCustomListsToListDto(customLists);
        assertEquals(2, result.getLists().size(), "The size of the lists should match");
        assertEquals("List 1", result.getLists().get(0).getTitle(), "Title should match for the first list");
        assertEquals("Description 1", result.getLists().get(0).getDescription(), "Description should match for the first list");
        assertEquals(10, result.getLists().get(0).getLikes(), "Likes should match for the first list");
        assertEquals(1L, result.getLists().get(0).getId(), "Id should match for the first list");
        assertEquals("List 2", result.getLists().get(1).getTitle(), "Title should match for the second list");
        assertEquals("Description 2", result.getLists().get(1).getDescription(), "Description should match for the second list");
        assertEquals(20, result.getLists().get(1).getLikes(), "Likes should match for the second list");
        assertEquals(2L, result.getLists().get(1).getId(), "Id should match for the second list");
    }

    @Test
    void convertToDisplayListDto_whenCalled_thenReturnsDisplayListDtoWithMappedValues() {
        CustomList customList = createCustomList(1L, "List 1", "Description 1", 10);
        Movie movie1 = createMovie(1L, "Movie 1", "Poster1.jpg", "2022");
        Movie movie2 = createMovie(2L, "Movie 2", "Poster2.jpg", "2023");
        customList.setMovies(Set.of(movie1, movie2));

        DisplayListDto result = listService.convertToDisplayListDto(customList);

        assertEquals("List 1", result.getTitle(), "Title should match");
        assertEquals("Description 1", result.getDescription(), "Description should match");
        assertEquals(10, result.getLikes(), "Likes should match");
        assertEquals(1L, result.getId(), "Id should match");
        assertEquals(2, result.getMovies().size(), "The size of the movies list should match");

        DisplayMovieInListDto movieDto1 = result.getMovies().get(0);
        assertEquals(1L, movieDto1.getId(), "Movie ID should match");
        assertEquals("Movie 1", movieDto1.getTitle(), "Movie title should match");
        assertEquals("Poster1.jpg", movieDto1.getPosterUrl(), "Movie poster URL should match");
        assertEquals("2022", movieDto1.getYear(), "Movie year should match");

        DisplayMovieInListDto movieDto2 = result.getMovies().get(1);
        assertEquals(2L, movieDto2.getId(), "Movie ID should match");
        assertEquals("Movie 2", movieDto2.getTitle(), "Movie title should match");
        assertEquals("Poster2.jpg", movieDto2.getPosterUrl(), "Movie poster URL should match");
        assertEquals("2023", movieDto2.getYear(), "Movie year should match");
    }

    @Test
    void convertToDisplayMovieInListDto_whenCalled_thenReturnsDisplayMovieInListDtoWithMappedValues() {
        Movie movie = createMovie(1L, "Movie 1", "Poster1.jpg", "2022");

        DisplayMovieInListDto result = listService.convertToDisplayMovieInListDto(movie);
        assertEquals(1L, result.getId(), "ID should match");
        assertEquals("Movie 1", result.getTitle(), "Title should match");
        assertEquals("Poster1.jpg", result.getPosterUrl(), "Poster URL should match");
        assertEquals("2022", result.getYear(), "Year should match");
    }


    @Test
    void findAllLists_whenCalled_thenReturnsListOfCustomLists() {
        CustomList list1 = createCustomList(1L, "List 1", "Description 1", 10);
        CustomList list2 = createCustomList(2L, "List 2", "Description 2", 20);
        when(customListRepository.findAll()).thenReturn(List.of(list1, list2));
        List<CustomList> result = listService.findAllLists();
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "List size should be 2");
        assertEquals("List 1", result.get(0).getTitle(), "Title should match for the first list");
        assertEquals("List 2", result.get(1).getTitle(), "Title should match for the second list");
    }

    @Test
    void findListByTitleAndDescription_whenCalled_thenReturnsCustomList() {
        FindListDto findListDto = new FindListDto();
        findListDto.setTitle("List 1");
        findListDto.setDescription("Description 1");
        CustomList customList = createCustomList(1L, "List 1", "Description 1", 10);
        when(customListRepository.findByTitleAndDescription("List 1", "Description 1")).thenReturn(customList);
        CustomList result = listService.findListByTitleAndDescription(findListDto);
        assertNotNull(result, "Result should not be null");
        assertEquals("List 1", result.getTitle(), "Title should match");
        assertEquals("Description 1", result.getDescription(), "Description should match");
    }

    @Test
    void findListById_whenCalled_thenReturnsOptionalOfCustomList() {
        CustomList customList = createCustomList(1L, "List 1", "Description 1", 10);
        when(customListRepository.findById(1L)).thenReturn(Optional.of(customList));
        Optional<CustomList> result = listService.findListById(1L);
        assertTrue(result.isPresent(), "Result should be present");
        assertEquals("List 1", result.get().getTitle(), "Title should match");
    }

    @Test
    void deleteList_whenCalled_thenDeletesCustomList() {
        CustomList customList = createCustomList(1L, "List 1", "Description 1", 10);
        listService.deleteList(customList);
        verify(customListRepository, times(1)).delete(customList);
    }

    @Test
    void findListByUsername_whenCalled_thenReturnsListOfCustomLists() {
        String username = "user1";
        CustomList list1 = createCustomList(1L, "List 1", "Description 1", 10);
        CustomList list2 = createCustomList(2L, "List 2", "Description 2", 20);
        when(customListRepository.findByUsername(username)).thenReturn(List.of(list1, list2));
        List<CustomList> result = listService.findListByUsername(username);
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "List size should be 2");
        assertEquals("List 1", result.get(0).getTitle(), "Title should match for the first list");
        assertEquals("List 2", result.get(1).getTitle(), "Title should match for the second list");
    }

    @Test
    void saveList_whenCalled_thenSavesCustomList() {
        CustomList customList = createCustomList(1L, "List 1", "Description 1", 10);
        listService.saveList(customList);
        verify(customListRepository, times(1)).saveAndFlush(customList);
    }



    private CustomList createCustomList(Long id, String title, String description, int likes) {
        CustomList customList = new CustomList();
        customList.setId(id);
        customList.setTitle(title);
        customList.setDescription(description);
        customList.setLikes(likes);
        customList.setMovies(new HashSet<>());
        return customList;
    }

    private Movie createMovie(Long id, String title, String posterUrl, String year) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setPosterUrl(posterUrl);
        movie.setYear(Year.parse(year));
        return movie;
    }
}
