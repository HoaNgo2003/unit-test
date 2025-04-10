package com.demo.service;

import com.demo.entities.Experience;
import com.demo.repositories.ExperienceRepository;
import com.demo.services.ExperienceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceImplTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    @Test
    void TC055_findAll_emptyList() {
        // Arrange
        when(experienceRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        Iterable<Experience> result = experienceService.findAll();
        List<Experience> resultList = StreamSupport.stream(result.spliterator(), false).toList();

        // Assert & Log
        int expectedSize = 0;
        int actualSize = resultList.size();
        System.out.println("TC055 - Expected: " + expectedSize + " | Actual: " + actualSize);
        assertEquals(expectedSize, actualSize);
    }

    @Test
    void TC056_findAll_nonEmptyList() {
        // Arrange
        Experience exp1 = new Experience();
        exp1.setId(1);
        exp1.setName("Fresher");

        Experience exp2 = new Experience();
        exp2.setId(2);
        exp2.setName("Senior");

        List<Experience> mockList = Arrays.asList(exp1, exp2);
        when(experienceRepository.findAll()).thenReturn(mockList);

        // Act
        Iterable<Experience> result = experienceService.findAll();
        List<Experience> resultList = StreamSupport.stream(result.spliterator(), false).toList();

        // Assert & Log
        int expectedSize = 2;
        int actualSize = resultList.size();
        System.out.println("TC056 - Expected: " + expectedSize + " | Actual: " + actualSize);
        assertEquals(expectedSize, actualSize);
    }


    @Test
    void TC057_delete_success() {
        // Arrange
        Experience exp = new Experience();
        exp.setId(1);
        when(experienceRepository.findById(1)).thenReturn(Optional.of(exp));
        doNothing().when(experienceRepository).delete(exp);

        // Act
        boolean result = experienceService.delete(1);

        // Assert & Log
        System.out.println("TC057 - Expected: true | Actual: " + result);
        assertTrue(result);
    }


    @Test
    void TC058_delete_failByDeleteException() {
        // Arrange
        Experience exp = new Experience();
        exp.setId(1);
        when(experienceRepository.findById(1)).thenReturn(Optional.of(exp));
        doThrow(new RuntimeException("Xóa thất bại")).when(experienceRepository).delete(exp);

        // Act
        boolean result = experienceService.delete(1);

        // Assert & Log
        System.out.println("TC058 - Expected: false | Actual: " + result);
        assertFalse(result);
    }


    @Test
    void TC059_delete_failByNotFound() {
        // Arrange
        when(experienceRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        boolean result = experienceService.delete(999);

        // Assert & Log
        System.out.println("TC059 - Expected: false | Actual: " + result);
        assertFalse(result);
    }

    @Test
    void TC060_exists_true() {
        // Arrange
        when(experienceRepository.exists("Java", 1)).thenReturn(1);

        // Act
        boolean result = experienceService.exists("Java", 1);

        // Assert & Log
        System.out.println("TC060 - Expected: true | Actual: " + result);
        assertTrue(result);
    }

    @Test
    void TC061_exists_false() {
        // Arrange
        when(experienceRepository.exists("Spring", 2)).thenReturn(0);

        // Act
        boolean result = experienceService.exists("Spring", 2);

        // Assert & Log
        System.out.println("TC061 - Expected: false | Actual: " + result);
        assertFalse(result);
    }

    @Test
    void TC062_save_success() {
        // Given
        Experience experience = new Experience();
        when(experienceRepository.save(experience)).thenReturn(experience);

        // When
        boolean actual = experienceService.save(experience);

        // Then
        System.out.printf("TC062 - Expected: true | Actual: %s%n", actual);
        assertTrue(actual);
    }


    @Test
    void TC063_save_failure() {
        // Arrange
        Experience exp = new Experience();
        doThrow(new RuntimeException("DB error")).when(experienceRepository).save(exp);

        // Act
        boolean result = experienceService.save(exp);

        // Assert & Log
        System.out.println("TC063 - Expected: false | Actual: " + result);
        assertFalse(result);
    }

    @Test
    void TC064_testFind_ExistingId_ReturnsExperience() {
        // Given
        int id = 1;
        Experience mockExperience = new Experience();
        mockExperience.setId(id);
        when(experienceRepository.findById(id)).thenReturn(Optional.of(mockExperience));

        // When
        Experience result = experienceService.find(id);

        // Then
        System.out.println("Expected: Experience with id = " + id);
        System.out.println("Actual: Experience with id = " + result.getId());

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(experienceRepository, times(1)).findById(id);
    }

//    @Test
//    void TC065_testFind_NonExistentId_ThrowsException() {
//        // Given
//        int nonExistentId = 999;
//        when(experienceRepository.findById(nonExistentId)).thenReturn(Optional.empty());
//
//        // When & Then
//        System.out.println("Expected: Throw NoSuchElementException");
//        try {
//            experienceService.find(nonExistentId);
//        } catch (Exception e) {
//            System.out.println("Actual: Thrown exception type = " + e.getClass().getSimpleName());
//        }
//
//        assertThrows(NoSuchElementException.class, () -> experienceService.find(nonExistentId));
//        verify(experienceRepository, times(1)).findById(nonExistentId);
//    }
//
//    @Test
//    void TC066_testFind_NegativeId_ThrowsException() {
//        // Given
//        int negativeId = -1;
//        when(experienceRepository.findById(negativeId)).thenReturn(Optional.empty());
//
//        // When & Then
//        System.out.println("Expected: Throw NoSuchElementException");
//        try {
//            experienceService.find(negativeId);
//        } catch (Exception e) {
//            System.out.println("Actual: Thrown exception type = " + e.getClass().getSimpleName());
//        }
//
//        assertThrows(NoSuchElementException.class, () -> experienceService.find(negativeId));
//        verify(experienceRepository, times(1)).findById(negativeId);
//    }


}
