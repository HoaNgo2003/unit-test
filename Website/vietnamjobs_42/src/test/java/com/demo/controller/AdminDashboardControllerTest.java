package com.demo.controller;

import com.demo.controllers.admin.AdminDashboardController;
import com.demo.entities.Employer;
import com.demo.services.AccountService;
import com.demo.services.ApplicationHistoryService;
import com.demo.services.EmployerService;
import com.demo.services.PostingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;

import java.text.DateFormatSymbols;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {
    @Mock
    private ModelMap model;

    // Capture dữ liệu được add vào model
    @Captor
    ArgumentCaptor<String> stringCaptor;

    @Captor
    ArgumentCaptor<Integer> intCaptor;

    @Captor
    ArgumentCaptor<List<Employer>> companyCaptor;

    private final int currentYear = Year.now().getValue();
    private static final int CURRENT_YEAR = 2025;

    private final String[] months = new DateFormatSymbols().getShortMonths();

    @InjectMocks
    private AdminDashboardController controller;

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    @Mock
    private AccountService accountService;

    @Mock
    private PostingService postingService;

    @Mock
    private EmployerService companyService;

    @Mock
    private ModelMap modelMap;

    @Test
    void TC067_index_ShouldAddStatisticsToModel_WhenServicesReturnValidData() {
        ModelMap model = new ModelMap();

        when(applicationHistoryService.countByResult(1)).thenReturn(1);
        when(applicationHistoryService.countByResult(2)).thenReturn(0);
        when(accountService.countByRoleAndMonthAndYear(1, 1, CURRENT_YEAR)).thenReturn(2);
        when(postingService.countByMonthAndYear(1, CURRENT_YEAR)).thenReturn(3);
        when(applicationHistoryService.countByMonthAndYear(1, CURRENT_YEAR)).thenReturn(4);
        when(companyService.findTop(5)).thenReturn(List.of(new Employer()));

        String viewName = controller.index(model);

        int applyPass = (int) model.get("applyPass");
        int applyFail = (int) model.get("applyFail");
        int seekerJan = (int) model.get("seekerJan");
        int postJan = (int) model.get("postJan");
        int applyJan = (int) model.get("applyJan");
        int companySize = ((List<?>) model.get("company")).size();

        System.out.println("Expect view: admin/dashboard/index | Actual: " + viewName);
        System.out.println("applyPass: expect=1 | actual=" + applyPass);
        System.out.println("applyFail: expect=0 | actual=" + applyFail);
        System.out.println("seekerJan: expect=2 | actual=" + seekerJan);
        System.out.println("postJan: expect=3 | actual=" + postJan);
        System.out.println("applyJan: expect=4 | actual=" + applyJan);
        System.out.println("company.size: expect=1 | actual=" + companySize);

        assertEquals("admin/dashboard/index", viewName);
        assertEquals(1, applyPass);
        assertEquals(0, applyFail);
        assertEquals(2, seekerJan);
        assertEquals(3, postJan);
        assertEquals(4, applyJan);
        assertEquals(1, companySize);
    }

    @Test
    void testIndex_TC068_emptyData() {
        // Arrange
        ModelMap model = new ModelMap(); // sử dụng ModelMap thật

        when(applicationHistoryService.countByResult(1)).thenReturn(0);
        when(applicationHistoryService.countByResult(2)).thenReturn(0);

        for (int month = 1; month <= 12; month++) {
            when(accountService.countByRoleAndMonthAndYear(1, month, currentYear)).thenReturn(0);
            when(postingService.countByMonthAndYear(month, currentYear)).thenReturn(0);
            when(applicationHistoryService.countByMonthAndYear(month, currentYear)).thenReturn(0);
        }

        List<Employer> emptyCompanyList = Collections.emptyList();
        when(companyService.findTop(5)).thenReturn(emptyCompanyList);

        // Act
        String view = controller.index(model);

        // Assert
        for (Object key : model.keySet()) {
            if (!"company".equals(key)) {
                Object actual = model.get(key);
                System.out.printf("Key: %-15s | Expected: 0 | Actual: %s%n", key, actual);
                assertEquals(0, actual, "Sai giá trị cho key: " + key);
            }
        }

        List<Employer> companies = (List<Employer>) model.get("company");
        System.out.println("company.size: " + (companies == null ? "null" : companies.size()));
        assertNotNull(companies);
        assertEquals(0, companies.size());

        System.out.println("Trả về view: " + view);
        assertEquals("admin/dashboard/index", view);
    }


    @Test
    void testIndex_TC069_partialMonthData() {
        ModelMap model = new ModelMap();

        when(applicationHistoryService.countByResult(1)).thenReturn(0);
        when(applicationHistoryService.countByResult(2)).thenReturn(0);

        for (int month = 1; month <= 12; month++) {
            when(accountService.countByRoleAndMonthAndYear(1, month, currentYear)).thenReturn(month == 2 ? 5 : 0);
            when(postingService.countByMonthAndYear(month, currentYear)).thenReturn(month == 3 ? 7 : 0);
            when(applicationHistoryService.countByMonthAndYear(month, currentYear)).thenReturn(month == 4 ? 2 : 0);
        }

        when(companyService.findTop(5)).thenReturn(Collections.emptyList());

        String view = controller.index(model);

        for (Object key : model.keySet()) {
            if (!"company".equals(key)) {
                Object actual = model.get(key);
                int expected = 0;
                if ("seekerFeb".equals(key)) expected = 5;
                if ("postMar".equals(key)) expected = 7;
                if ("applyApr".equals(key)) expected = 2;
                System.out.printf("Key: %-15s | Expected: %d | Actual: %s\n", key, expected, actual);
                assertEquals(expected, actual, "Sai giá trị cho key: " + key);
            }
        }

        List<Employer> companies = (List<Employer>) model.get("company");
        System.out.println("company.size: " + (companies == null ? "null" : companies.size()));
        assertNotNull(companies);
        assertEquals(0, companies.size());

        System.out.println("Trả về view: " + view);
        assertEquals("admin/dashboard/index", view);
    }

    @Test
    void testIndex_TC070_manyCompanies() {
        ModelMap model = new ModelMap();

        when(applicationHistoryService.countByResult(1)).thenReturn(0);
        when(applicationHistoryService.countByResult(2)).thenReturn(0);

        for (int month = 1; month <= 12; month++) {
            when(accountService.countByRoleAndMonthAndYear(1, month, currentYear)).thenReturn(0);
            when(postingService.countByMonthAndYear(month, currentYear)).thenReturn(0);
            when(applicationHistoryService.countByMonthAndYear(month, currentYear)).thenReturn(0);
        }

        List<Employer> companiesMock = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            companiesMock.add(new Employer());
        }
        when(companyService.findTop(5)).thenReturn(companiesMock);

        String view = controller.index(model);

        for (Object key : model.keySet()) {
            if (!"company".equals(key)) {
                Object actual = model.get(key);
                System.out.printf("Key: %-15s | Expected: 0 | Actual: %s\n", key, actual);
                assertEquals(0, actual);
            }
        }

        List<Employer> companies = (List<Employer>) model.get("company");
        System.out.println("company.size: " + (companies == null ? "null" : companies.size()));
        assertNotNull(companies);
        assertEquals(5, companies.size());

        System.out.println("Trả về view: " + view);
        assertEquals("admin/dashboard/index", view);
    }

    @Test
    void testIndex_TC071_mixedData() {
        ModelMap model = new ModelMap();

        when(applicationHistoryService.countByResult(1)).thenReturn(2);
        when(applicationHistoryService.countByResult(2)).thenReturn(1);

        for (int month = 1; month <= 12; month++) {
            when(accountService.countByRoleAndMonthAndYear(1, month, currentYear)).thenReturn(month == 7 ? 4 : 0);
            when(postingService.countByMonthAndYear(month, currentYear)).thenReturn(month == 6 ? 6 : 0);
            when(applicationHistoryService.countByMonthAndYear(month, currentYear)).thenReturn(month == 8 ? 7 : 0);
        }

        when(companyService.findTop(5)).thenReturn(Collections.emptyList());

        String view = controller.index(model);

        assertEquals(2, model.get("applyPass"));
        assertEquals(1, model.get("applyFail"));
        assertEquals(4, model.get("seekerJul"));
        assertEquals(6, model.get("postJun"));
        assertEquals(7, model.get("applyAug"));

        System.out.printf("applyPass: %s\napplyFail: %s\npostJun: %s\nseekerJul: %s\napplyAug: %s\n",
                model.get("applyPass"), model.get("applyFail"), model.get("postJun"), model.get("seekerJul"), model.get("applyAug"));

        List<Employer> companies = (List<Employer>) model.get("company");
        System.out.println("company.size: " + (companies == null ? "null" : companies.size()));
        assertNotNull(companies);
        assertEquals(0, companies.size());

        System.out.println("Trả về view: " + view);
        assertEquals("admin/dashboard/index", view);
    }





    @Test
    void testIndex_TC072_largeDataVolume() {
        ModelMap model = new ModelMap();

        when(applicationHistoryService.countByResult(1)).thenReturn(10000);
        when(applicationHistoryService.countByResult(2)).thenReturn(10000);

        for (int month = 1; month <= 12; month++) {
            when(accountService.countByRoleAndMonthAndYear(1, month, currentYear)).thenReturn(10000);
            when(postingService.countByMonthAndYear(month, currentYear)).thenReturn(10000);
            when(applicationHistoryService.countByMonthAndYear(month, currentYear)).thenReturn(10000);
        }

        List<Employer> employers = new ArrayList<>();
        for (int i = 0; i < 100; i++) employers.add(new Employer());
        when(companyService.findTop(5)).thenReturn(employers);

        String view = controller.index(model);

        assertEquals("admin/dashboard/index", view);
        assertEquals(10000, model.get("applyPass"));
        assertEquals(10000, model.get("applyFail"));

        List<Employer> companies = (List<Employer>) model.get("company");
        assertNotNull(companies);
        assertEquals(100, companies.size());
    }

    @Test
    void testIndex_TC073_nullModelMap() {
        assertThrows(NullPointerException.class, () -> controller.index(null));
    }










}
