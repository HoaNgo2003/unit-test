package com.demo.controller;

import com.demo.controllers.admin.AdminExperienceController;
import com.demo.entities.Experience;
import com.demo.repositories.PostingRepository;
import com.demo.services.ExperienceService;
import com.demo.services.PostingService;
import com.demo.services.PostingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminExperienceControllerTest {

    @Mock
    private PostingService postingService; // DÙNG INTERFACE

    @Mock
    private ExperienceService experienceService;

    @InjectMocks
    private AdminExperienceController controller;

    private RedirectAttributes redirectAttributes;
    private ModelMap model;

    @BeforeEach
    void setUp() {
        model = new ModelMap();
        redirectAttributes = new RedirectAttributesModelMap();
    }


    // TC041 - Khi có dữ liệu
    @Test
    void testcase_TC041_Index_WithData_ShouldReturnViewAndModelFilled() {
        Experience e1 = new Experience();
        Experience e2 = new Experience();
        when(experienceService.findAll()).thenReturn(Arrays.asList(e1, e2));

        String view = controller.index(model);

        System.out.println("[TC041] ModelMap trước khi gọi: {}");
        System.out.println("[TC041] Gọi index(), view trả về: " + view);
        System.out.println("[TC041] ModelMap sau khi gọi: " + model);

        assertEquals("admin/experience/index", view);
        assertTrue(model.containsAttribute("newItem"));
        assertTrue(model.containsAttribute("updateItem"));
        assertTrue(model.containsAttribute("experienceList"));

        Iterable<?> list = (Iterable<?>) model.get("experienceList");
        assertEquals(2, ((Collection<?>) list).size());
    }

    // TC042 - Khi không có dữ liệu
    @Test
    void testcase_TC042_Index_WithEmptyList_ShouldReturnViewWithEmptyExperienceList() {
        when(experienceService.findAll()).thenReturn(Collections.emptyList());

        String view = controller.index(model);

        System.out.println("[TC042] ModelMap trước khi gọi: {}");
        System.out.println("[TC042] Gọi index(), view trả về: " + view);
        System.out.println("[TC042] ModelMap sau khi gọi: " + model);

        assertEquals("admin/experience/index", view);
        assertTrue(model.containsAttribute("newItem"));
        assertTrue(model.containsAttribute("updateItem"));
        assertTrue(model.containsAttribute("experienceList"));

        Iterable<?> list = (Iterable<?>) model.get("experienceList");
        assertFalse(list.iterator().hasNext());
    }

    @Test
    void testcase_TC043_Delete_WhenExperienceIsUsedInPost_ShouldRedirectWithExistPostFlash() {
        int id = 1;

        when(postingService.existPostByExperienceId(id)).thenReturn(true);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.delete(id, redirectAttributes);

        System.out.println("[TC043] Gọi delete với id đang được sử dụng trong post.");
        System.out.println("[TC043] Kết quả view trả về: " + result);
        System.out.println("[TC043] Flash attributes: " + redirectAttributes.getFlashAttributes());

        assertEquals("redirect:/admin/experience", result);
        assertEquals("Không thể xóa. Bạn có thể sửa trạng thái sang ẩn.", redirectAttributes.getFlashAttributes().get("existPost"));
    }

    @Test
    void testcase_TC044_Delete_WhenNotUsedAndDeleteSuccess_ShouldRedirectWithSuccessFlash() {
        int id = 2;

        when(postingService.existPostByExperienceId(id)).thenReturn(false);
        when(experienceService.delete(id)).thenReturn(true);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.delete(id, redirectAttributes);

        System.out.println("[TC044] Gọi delete với id KHÔNG dùng và xóa THÀNH CÔNG.");
        System.out.println("[TC044] Kết quả view trả về: " + result);
        System.out.println("[TC044] Flash attributes: " + redirectAttributes.getFlashAttributes());

        assertEquals("redirect:/admin/experience", result);
        assertEquals("Thành công!", redirectAttributes.getFlashAttributes().get("success"));
    }

    @Test
    void testcase_TC045_Delete_WhenNotUsedAndDeleteFail_ShouldRedirectWithErrorFlash() {
        int id = 3;

        when(postingService.existPostByExperienceId(id)).thenReturn(false);
        when(experienceService.delete(id)).thenReturn(false);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.delete(id, redirectAttributes);

        System.out.println("[TC045] Gọi delete với id KHÔNG dùng và xóa THẤT BẠI.");
        System.out.println("[TC045] Kết quả view trả về: " + result);
        System.out.println("[TC045] Flash attributes: " + redirectAttributes.getFlashAttributes());

        assertEquals("redirect:/admin/experience", result);
        assertEquals("Thất bại...", redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    void testcase_TC046_HandleAdd_WhenNameExists_ShouldRedirectWithExistFlash() {
        Experience newItem = new Experience();
        newItem.setName("Trưởng phòng");

        when(experienceService.exists(newItem.getName(), 0)).thenReturn(true);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String result = controller.handleAdd(newItem, redirectAttributes);

        System.out.println("[TC046] Tên đã tồn tại → Flash: " + redirectAttributes.getFlashAttributes());

        assertEquals("redirect:/admin/experience", result);
        assertEquals("Đã tồn tại.", redirectAttributes.getFlashAttributes().get("exist"));
    }

    @Test
    void testcase_TC047_HandleAdd_WhenSaveSuccess_ShouldRedirectWithSuccessFlash() {
        Experience newItem = new Experience();
        newItem.setName("Chuyên viên");

        when(experienceService.exists(newItem.getName(), 0)).thenReturn(false);
        when(experienceService.save(newItem)).thenReturn(true);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String result = controller.handleAdd(newItem, redirectAttributes);

        System.out.println("[TC047] Save thành công → Flash: " + redirectAttributes.getFlashAttributes());

        assertEquals("redirect:/admin/experience", result);
        assertEquals("Thành công!", redirectAttributes.getFlashAttributes().get("success"));
    }

    @Test
    void testcase_TC048_HandleAdd_WhenSaveFail_ShouldRedirectWithErrorFlash() {
        Experience newItem = new Experience();
        newItem.setName("Thực tập sinh");

        when(experienceService.exists(newItem.getName(), 0)).thenReturn(false);
        when(experienceService.save(newItem)).thenReturn(false);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String result = controller.handleAdd(newItem, redirectAttributes);

        System.out.println("[TC048] Save thất bại → Flash: " + redirectAttributes.getFlashAttributes());

        assertEquals("redirect:/admin/experience", result);
        assertEquals("Thất bại...", redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    void testcase_TC049_HandleAdd_WhenExceptionOccurs_ShouldRedirectWithErrorFlash() {
        Experience newItem = new Experience();
        newItem.setName("CEO");

        when(experienceService.exists(anyString(), anyInt())).thenThrow(new RuntimeException("Lỗi bất ngờ"));

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String result = controller.handleAdd(newItem, redirectAttributes);

        System.out.println("[TC049] Ngoại lệ xảy ra → Flash: " + redirectAttributes.getFlashAttributes());

        assertEquals("redirect:/admin/experience", result);
        assertEquals("Thất bại...", redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    void TC050_updateExperience_notFound() {
        // Arrange
        Experience updateItem = new Experience();
        updateItem.setId(1);

        when(experienceService.find(1)).thenReturn(null);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        String result = controller.update(updateItem, redirectAttributes);

        // Assert
        verify(redirectAttributes).addFlashAttribute("notFound", "Không tìm thấy...");
        System.out.println("🔍 [TC050] Expected: redirect:/admin/experience | Actual: " + result);
        assertEquals("redirect:/admin/experience", result);
    }

    @Test
    void TC051_updateExperience_nameExists() {
        // Arrange
        Experience updateItem = new Experience();
        updateItem.setId(2);
        updateItem.setName("Trung cấp");

        when(experienceService.find(2)).thenReturn(new Experience());
        when(experienceService.exists("Trung cấp", 2)).thenReturn(true);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        String result = controller.update(updateItem, redirectAttributes);

        // Assert
        verify(redirectAttributes).addFlashAttribute("exist", "Đã tồn tại.");
        System.out.println("🔍 [TC051] Expected: redirect:/admin/experience | Actual: " + result);
        assertEquals("redirect:/admin/experience", result);
    }

    @Test
    void TC052_updateExperience_success() {
        // Arrange
        Experience updateItem = new Experience();
        updateItem.setId(3);
        updateItem.setName("Cao cấp");

        when(experienceService.find(3)).thenReturn(new Experience());
        when(experienceService.exists("Cao cấp", 3)).thenReturn(false);
        when(experienceService.save(updateItem)).thenReturn(true);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        String result = controller.update(updateItem, redirectAttributes);

        // Assert
        verify(redirectAttributes).addFlashAttribute("success", "Thành công!");
        System.out.println(" [TC052] Expected: redirect:/admin/experience | Actual: " + result);
        assertEquals("redirect:/admin/experience", result);
    }

    @Test
    void TC053_updateExperience_saveFailed() {
        // Arrange
        Experience updateItem = new Experience();
        updateItem.setId(4);
        updateItem.setName("Thử nghiệm");

        when(experienceService.find(4)).thenReturn(new Experience());
        when(experienceService.exists("Thử nghiệm", 4)).thenReturn(false);
        when(experienceService.save(updateItem)).thenReturn(false);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        String result = controller.update(updateItem, redirectAttributes);

        // Assert
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
        System.out.println(" [TC053] Expected: redirect:/admin/experience | Actual: " + result);
        assertEquals("redirect:/admin/experience", result);
    }

    @Test
    void TC054_updateExperience_exceptionThrown() {
        // Arrange
        Experience updateItem = new Experience();
        updateItem.setId(5);
        updateItem.setName("Xảy ra lỗi");

        when(experienceService.find(5)).thenThrow(new RuntimeException("Lỗi bất ngờ"));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        String result = controller.update(updateItem, redirectAttributes);

        // Assert
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
        System.out.println(" [TC054] Expected: redirect:/admin/experience | Actual: " + result);
        assertEquals("redirect:/admin/experience", result);
    }





}
