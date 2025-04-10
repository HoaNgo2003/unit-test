package com.demo.controller;

import com.demo.controllers.admin.AdminJobController;
import com.demo.entities.Employer;
import com.demo.entities.Postings;

import com.demo.services.EmployerService;
import com.demo.services.PostingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminJobControllerTest {

    @Mock
    private PostingService jobService;

    @Mock
    private EmployerService companyService;

    @InjectMocks
    private AdminJobController controller;

    private ModelMap model;

    private RedirectAttributes redirectAttributes;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        model = new ModelMap();
        redirectAttributes = new RedirectAttributesModelMap();
    }

    @Test
    void testcase_TC0015_Index_WhenJobListExists_ShouldReturnCorrectViewAndModel() {
        // ===== Trạng thái trước =====
        assertTrue(model.isEmpty(), "ModelMap ban đầu phải rỗng");

        // ===== Giả lập dữ liệu =====
        Postings job1 = new Postings(); job1.setId(1);
        Postings job2 = new Postings(); job2.setId(2);
        when(jobService.getAll()).thenReturn(Arrays.asList(job1, job2));

        // ===== Gọi hàm =====
        String view = controller.index(model);

        // ===== Kiểm tra kết quả =====
        assertEquals("admin/job/index", view);
        assertTrue(model.containsAttribute("jobList"));
        Iterable<?> jobList = (Iterable<?>) model.get("jobList");
        assertEquals(2, jobList.spliterator().getExactSizeIfKnown());

        // ===== Trạng thái sau =====
        System.out.println("[TC001] Số lượng job trong model: " + jobList.spliterator().getExactSizeIfKnown());
        System.out.println("[TC001] View trả về: " + view);
    }


    @Test
    void testcase_TC0016_Index_WhenJobListIsEmpty_ShouldReturnViewWithEmptyList() {
        // ===== Trạng thái trước =====
        assertTrue(model.isEmpty(), "ModelMap ban đầu phải rỗng");

        // ===== Giả lập dữ liệu =====
        when(jobService.getAll()).thenReturn(Collections.emptyList());

        // ===== Gọi hàm =====
        String view = controller.index(model);

        // ===== Kiểm tra kết quả =====
        assertEquals("admin/job/index", view);
        assertTrue(model.containsAttribute("jobList"));
        Iterable<?> jobList = (Iterable<?>) model.get("jobList");
        assertFalse(jobList.iterator().hasNext(), "Danh sách job phải rỗng");

        // ===== Trạng thái sau =====
        System.out.println("[TC002] Danh sách job rỗng: " + !jobList.iterator().hasNext());
        System.out.println("[TC002] View trả về: " + view);
    }

    @Test
    void testcase_TC0017_Company_WhenValidId_ShouldReturnCompanyAndJobList() {
        int id = 1;

        Employer company = new Employer();
        company.setId(id);
        Postings job1 = new Postings();
        Postings job2 = new Postings();

        when(companyService.getDetail(id)).thenReturn(company);
        when(jobService.getByEmployerId(id)).thenReturn(Arrays.asList(job1, job2));

        String view = controller.company(model, id);

        // Kiểm tra
        assertEquals("admin/job/company", view);
        assertTrue(model.containsAttribute("company"));
        assertTrue(model.containsAttribute("jobList"));
        assertEquals(company, model.get("company"));
        assertEquals(2, ((Iterable<?>) model.get("jobList")).spliterator().getExactSizeIfKnown());

        System.out.println("[TC001] Company ID: " + ((Employer) model.get("company")).getId());
        System.out.println("[TC001] Số lượng job: " + ((Iterable<?>) model.get("jobList")).spliterator().getExactSizeIfKnown());
    }
    @Test
    void testcase_TC0018_Company_WhenNoJobs_ShouldReturnEmptyJobList() {
        int id = 2;

        Employer company = new Employer();
        company.setId(id);

        when(companyService.getDetail(id)).thenReturn(company);
        when(jobService.getByEmployerId(id)).thenReturn(Collections.emptyList());

        String view = controller.company(model, id);

        assertEquals("admin/job/company", view);
        assertTrue(model.containsAttribute("company"));
        assertTrue(model.containsAttribute("jobList"));
        assertEquals(company, model.get("company"));
        assertFalse(((Iterable<?>) model.get("jobList")).iterator().hasNext());

        System.out.println("[TC002] JobList rỗng, company vẫn có ID: " + ((Employer) model.get("company")).getId());
    }

    @Test
    void testcase_TC0019_Company_WhenCompanyNotFound_ShouldHandleNullCompany() {
        int id = 3;

        when(companyService.getDetail(id)).thenReturn(null);
        when(jobService.getByEmployerId(id)).thenReturn(Collections.emptyList());

        String view = controller.company(model, id);

        assertEquals("admin/job/company", view);
        assertTrue(model.containsAttribute("company"));
        assertTrue(model.containsAttribute("jobList"));
        assertNull(model.get("company"));
        assertFalse(((Iterable<?>) model.get("jobList")).iterator().hasNext());

        System.out.println("[TC003] Company null, jobList rỗng. View trả về: " + view);
    }

    // TC020 - Truyền id hợp lệ, service trả về 1 job
    @Test
    void testcase_TC020_Details_WithValidId_ShouldReturnViewWithJobData() {
        int jobId = 10;
        Postings mockJob = new Postings();
        mockJob.setId(jobId);

        when(jobService.getDetail(jobId)).thenReturn(mockJob);

        String view = controller.details(jobId, model);

        assertEquals("admin/job/detail", view);
        assertTrue(model.containsAttribute("item"));
        assertEquals(mockJob, model.get("item"));
        System.out.println("[TC020] View: " + view + " | Job ID: " + ((Postings) model.get("item")).getId());
    }

    // TC021 - Truyền id không tồn tại, service trả về null
    @Test
    void testcase_TC021_Details_WithInvalidId_ShouldReturnViewWithNullItem() {
        int jobId = 99;

        when(jobService.getDetail(jobId)).thenReturn(null);

        String view = controller.details(jobId, model);

        assertEquals("admin/job/detail", view);
        assertTrue(model.containsAttribute("item"));
        assertNull(model.get("item"));
        System.out.println("[TC021] View: " + view + " | Job is null: " + (model.get("item") == null));
    }

    @Test
    void testcase_TC022_UpdateStatusFromCompany_WhenJobNotFound_ShouldRedirectWithNotFoundMessage() {
        int id = 10, companyId = 5;
        when(jobService.find(id)).thenReturn(null);

        System.out.println("[TC022] Trước khi gọi controller: jobService.find(" + id + ") == null");

        String result = controller.updateStatusFromCompany(id, companyId, redirectAttributes);

        System.out.println("[TC022] Sau khi gọi controller: redirect = " + result);
        System.out.println("[TC022] Flash message: notFound = " + redirectAttributes.getFlashAttributes().get("notFound"));

        assertEquals("redirect:/admin/job/" + companyId, result);
        assertEquals("Không tìm thấy...", redirectAttributes.getFlashAttributes().get("notFound"));
    }


    @Test
    void testcase_TC023_UpdateStatusFromCompany_WhenUpdateSuccess_ShouldRedirectWithSuccessMessage() {
        int id = 6, companyId = 3;
        Postings job = new Postings();
        job.setId(id);
        job.setStatus(true); // Trước khi update

        when(jobService.find(id)).thenReturn(job);
        when(jobService.updateStatusById(id, false)).thenReturn(true);

        System.out.println("[TC023] Trước update: status = " + job.isStatus());

        String result = controller.updateStatusFromCompany(id, companyId, redirectAttributes);

        System.out.println("[TC023] Sau update: status = " + job.isStatus());

        assertEquals("redirect:/admin/job/" + companyId, result);
        assertEquals("Thành công!", redirectAttributes.getFlashAttributes().get("success"));
        assertFalse(job.isStatus());
    }

    @Test
    void testcase_TC024_UpdateStatusFromCompany_WhenUpdateFails_ShouldRedirectWithErrorMessage() {
        int id = 7, companyId = 3;
        Postings job = new Postings();
        job.setId(id);
        job.setStatus(false); // Trước khi update

        when(jobService.find(id)).thenReturn(job);
        when(jobService.updateStatusById(id, true)).thenReturn(false);

        System.out.println("[TC024] Trước update: status = " + job.isStatus());

        String result = controller.updateStatusFromCompany(id, companyId, redirectAttributes);

        System.out.println("[TC024] Sau update: status = " + job.isStatus());

        assertEquals("redirect:/admin/job/" + companyId, result);
        assertEquals("Thất bại...", redirectAttributes.getFlashAttributes().get("error"));
        assertTrue(job.isStatus());
    }

    @Test
    void testcase_TC025_UpdateStatusFromCompany_WhenExceptionOccurs_ShouldRedirectWithErrorMessage() {
        int id = 8, companyId = 4;
        when(jobService.find(id)).thenThrow(new RuntimeException("Bùm!"));

        String result = controller.updateStatusFromCompany(id, companyId, redirectAttributes);

        assertEquals("redirect:/admin/job/" + companyId, result);
        assertEquals("Thất bại...", redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    void testcase_TC026_UpdateStatus_WhenJobNotFoundAndIsDetailTrue_ShouldRedirectToDetailWithNotFoundMessage() {
        int id = 10;
        boolean isDetail = true;

        when(jobService.find(id)).thenReturn(null);

        String result = controller.updateStatus(id, isDetail, redirectAttributes);

        String expectedRedirect = "redirect:/admin/job/detail/" + id;
        String expectedMessage = "Không tìm thấy...";
        String actualMessage = (String) redirectAttributes.getFlashAttributes().get("notFound");

        System.out.println("[TC026] Kết quả trả về: " + result);
        System.out.println("[TC026] Flash Message: " + actualMessage);

        assertEquals(expectedRedirect, result);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testcase_TC027_UpdateStatus_WhenJobNotFoundAndIsDetailFalse_ShouldRedirectToListWithNotFoundMessage() {
        int id = 11;
        boolean isDetail = false;

        when(jobService.find(id)).thenReturn(null);

        String result = controller.updateStatus(id, isDetail, redirectAttributes);

        String expectedRedirect = "redirect:/admin/job";
        String expectedMessage = "Không tìm thấy...";
        String actualMessage = (String) redirectAttributes.getFlashAttributes().get("notFound");

        System.out.println("[TC027] Kết quả trả về: " + result);
        System.out.println("[TC027] Flash Message: " + actualMessage);

        assertEquals(expectedRedirect, result);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testcase_TC028_UpdateStatus_WhenUpdateSuccessAndIsDetailTrue_ShouldToggleStatusAndRedirectWithSuccess() {
        int id = 12;
        boolean isDetail = true;

        Postings job = new Postings();
        job.setId(id);
        job.setStatus(true); // trạng thái ban đầu: true

        when(jobService.find(id)).thenReturn(job);
        when(jobService.updateStatusById(id, false)).thenReturn(true); // chuyển sang false

        System.out.println("[TC028] Trạng thái trước: " + job.isStatus());
        String result = controller.updateStatus(id, isDetail, redirectAttributes);
        System.out.println("[TC028] Trạng thái sau: " + job.isStatus());

        String expectedRedirect = "redirect:/admin/job/detail/" + id;
        String expectedMessage = "Thành công!";
        String actualMessage = (String) redirectAttributes.getFlashAttributes().get("success");

        assertEquals(expectedRedirect, result);
        assertEquals(expectedMessage, actualMessage);
        assertFalse(job.isStatus()); // trạng thái mới: false
    }

    @Test
    void testcase_TC029_UpdateStatus_WhenUpdateFailsAndIsDetailFalse_ShouldToggleStatusAndRedirectWithError() {
        int id = 13;
        boolean isDetail = false;

        Postings job = new Postings();
        job.setId(id);
        job.setStatus(false); // trạng thái ban đầu: false

        when(jobService.find(id)).thenReturn(job);
        when(jobService.updateStatusById(id, true)).thenReturn(false); // cố gắng bật trạng thái → fail

        System.out.println("[TC029] Trạng thái trước: " + job.isStatus());
        String result = controller.updateStatus(id, isDetail, redirectAttributes);
        System.out.println("[TC029] Trạng thái sau: " + job.isStatus());

        String expectedRedirect = "redirect:/admin/job";
        String expectedMessage = "Thất bại...";
        String actualMessage = (String) redirectAttributes.getFlashAttributes().get("error");

        assertEquals(expectedRedirect, result);
        assertEquals(expectedMessage, actualMessage);
        assertTrue(job.isStatus()); // trạng thái vẫn đổi (trong object), nhưng service fail
    }
}
