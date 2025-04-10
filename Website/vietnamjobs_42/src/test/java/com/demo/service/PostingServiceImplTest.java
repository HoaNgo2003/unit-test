package com.demo.service;

import com.demo.entities.Postings;
import com.demo.repositories.PostingRepository;
import com.demo.services.PostingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostingServiceImplTest {

    @Mock
    private PostingRepository postingRepository;

    @InjectMocks
    private PostingServiceImpl postingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testcase_TC030_getAll_WhenPostingsExist_ShouldReturnListOfPostings() {
        // Dữ liệu trước test
        Postings post1 = new Postings(); post1.setId(1);
        Postings post2 = new Postings(); post2.setId(2);
        List<Postings> mockList = Arrays.asList(post1, post2);

        when(postingRepository.findAll()).thenReturn(mockList);

        // Thực thi
        Iterable<Postings> result = postingService.getAll();

        // Dữ liệu sau test
        List<Postings> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        System.out.println("[TC030] Trước test: postingRepository trả về danh sách 2 phần tử.");
        System.out.println("[TC030] Sau test: postingService trả về danh sách size = " + resultList.size());

        assertEquals(2, resultList.size());
        assertEquals(1, resultList.get(0).getId());
        assertEquals(2, resultList.get(1).getId());
    }

    @Test
    void testcase_TC031_getAll_WhenNoPostingsExist_ShouldReturnEmptyList() {
        when(postingRepository.findAll()).thenReturn(Collections.emptyList());

        Iterable<Postings> result = postingService.getAll();
        List<Postings> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        System.out.println("[TC031] Trước test: postingRepository trả về danh sách rỗng.");
        System.out.println("[TC031] Sau test: postingService trả về danh sách size = " + resultList.size());

        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    void testcase_TC032_getByEmployerId_WhenPostingsExist_ShouldReturnCorrectList() {
        int employerId = 5;
        Postings post1 = new Postings(); post1.setId(1);
        Postings post2 = new Postings(); post2.setId(2);

        when(postingRepository.getByEmployerId(employerId)).thenReturn(Arrays.asList(post1, post2));

        Iterable<Postings> result = postingService.getByEmployerId(employerId);

        List<Postings> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        System.out.println("[TC032] Trước test: repository trả về danh sách 2 bài đăng.");
        System.out.println("[TC032] Sau test: service trả về danh sách size = " + resultList.size());

        assertEquals(2, resultList.size());
        assertEquals(1, resultList.get(0).getId());
        assertEquals(2, resultList.get(1).getId());
    }

    @Test
    void testcase_TC033_getByEmployerId_WhenNoPostings_ShouldReturnEmptyList() {
        int employerId = 99;
        when(postingRepository.getByEmployerId(employerId)).thenReturn(Collections.emptyList());

        Iterable<Postings> result = postingService.getByEmployerId(employerId);

        List<Postings> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        System.out.println("[TC033] Trước test: repository trả về danh sách rỗng.");
        System.out.println("[TC033] Sau test: service trả về danh sách size = " + resultList.size());

        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    void testcase_TC034_GetDetail_WhenIdExists_ShouldReturnPostings() {
        int id = 10;
        Postings expectedPost = new Postings();
        expectedPost.setId(id);

        when(postingRepository.findById(id)).thenReturn(Optional.of(expectedPost));

        Postings actual = postingService.getDetail(id);

        assertNotNull(actual);
        assertEquals(id, actual.getId());
    }

    @Test
    void testcase_TC035_GetDetail_WhenIdNotExists_ShouldThrowException() {
        int id = 99;

        when(postingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> postingService.getDetail(id));
    }

    @Test
    void testcase_TC036_Find_WhenIdIsValid_ShouldReturnCorrectPostings() {
        int id = 5;
        Postings expectedPost = new Postings();
        expectedPost.setId(id);

        when(postingRepository.findById(id)).thenReturn(Optional.of(expectedPost));

        Postings result = postingService.find(id);

        System.out.println("[TC037] ID input: " + id);
        System.out.println("[TC037] Expected ID: " + expectedPost.getId() + " | Actual: " + result.getId());

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    // TC038 - Khi id không tồn tại
    @Test
    void testcase_TC037_Find_WhenIdIsNotFound_ShouldThrowException() {
        int id = 9999;
        when(postingRepository.findById(id)).thenReturn(Optional.empty());

        System.out.println("[TC038] ID input: " + id);
        Exception exception = assertThrows(
                java.util.NoSuchElementException.class,
                () -> postingService.find(id)
        );

        System.out.println("[TC038] Expected exception: NoSuchElementException | Actual: " + exception.getClass().getSimpleName());
    }

    // TC037 - Update thành công
    @Test
    void testcase_TC038_UpdateStatus_WhenRepositoryReturnsPositive_ShouldReturnTrue() {
        int id = 1;
        boolean status = true;

        when(postingRepository.updateStatusById(id, status)).thenReturn(1);

        boolean result = postingService.updateStatusById(id, status);

        System.out.println("[TC037] ID: " + id + ", Status: " + status);
        System.out.println("[TC037] Repository trả về: 1 → Kết quả mong muốn: true | Kết quả thực tế: " + result);

        assertTrue(result);
    }

    // TC038 - Update thất bại
    @Test
    void testcase_TC039_UpdateStatus_WhenRepositoryReturnsZero_ShouldReturnFalse() {
        int id = 999;
        boolean status = false;

        when(postingRepository.updateStatusById(id, status)).thenReturn(0);

        boolean result = postingService.updateStatusById(id, status);

        System.out.println("[TC038] ID: " + id + ", Status: " + status);
        System.out.println("[TC038] Repository trả về: 0 → Kết quả mong muốn: false | Kết quả thực tế: " + result);

        assertFalse(result);
    }

    // TC039 - Khi có exception
    @Test
    void testcase_TC040_UpdateStatus_WhenRepositoryThrowsException_ShouldPropagateException() {
        int id = -1;
        boolean status = true;

        when(postingRepository.updateStatusById(id, status)).thenThrow(new RuntimeException("Lỗi DB"));

        System.out.println("[TC039] ID: " + id + ", Status: " + status + " → Kỳ vọng lỗi RuntimeException");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            postingService.updateStatusById(id, status);
        });

        System.out.println("[TC039] Kết quả thực tế: " + exception.getMessage());

        assertEquals("Lỗi DB", exception.getMessage());
    }

}
