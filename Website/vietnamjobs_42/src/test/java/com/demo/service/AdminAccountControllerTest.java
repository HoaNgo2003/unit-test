package com.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.controllers.admin.AdminAccountController;
import com.demo.entities.Account;
import com.demo.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.*;
import java.util.stream.StreamSupport;

class AdminAccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AdminAccountController controller;



    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redirectAttributes = new RedirectAttributesModelMap();
    }

    @Test
    void testcase_TC001_UpdateStatus_WhenAccountNotFound_ShouldRedirectWithNotFoundMessage() {
        int id = 1;
        when(accountService.find(id)).thenReturn(null);

        String result = controller.updateStatus(id, redirectAttributes);
        String expectedRedirect = "redirect:/admin/account";
        String expectedMessage = "Không tìm thấy...";
        String actualMessage = (String) redirectAttributes.getFlashAttributes().get("notFound");

        System.out.println("[TC001] Expected redirect: " + expectedRedirect + " | Actual: " + result);
        System.out.println("[TC001] Expected flash message: " + expectedMessage + " | Actual: " + actualMessage);

        assertEquals(expectedRedirect, result);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testcase_TC002_UpdateStatus_WhenUpdateSuccess_ShouldRedirectWithSuccessMessage() {
        int id = 2;
        Account account = new Account();
        account.setId(id);
        account.setStatus(true); // old status = true → new = false

        when(accountService.find(id)).thenReturn(account);
        when(accountService.updateStatusById(id, false)).thenReturn(true);

        String result = controller.updateStatus(id, redirectAttributes);
        String expectedRedirect = "redirect:/admin/account";
        String expectedMessage = "Thành công!";
        String actualMessage = (String) redirectAttributes.getFlashAttributes().get("success");

        System.out.println("[TC002] Expected redirect: " + expectedRedirect + " | Actual: " + result);
        System.out.println("[TC002] Expected flash message: " + expectedMessage + " | Actual: " + actualMessage);
        System.out.println("[TC002] Expected new status: false | Actual: " + account.isStatus());

        assertEquals(expectedRedirect, result);
        assertEquals(expectedMessage, actualMessage);
        assertFalse(account.isStatus());
    }

    @Test
    void testcase_TC003_UpdateStatus_WhenUpdateFails_ShouldRedirectWithErrorMessage() {
        int id = 3;
        Account account = new Account();
        account.setId(id);
        account.setStatus(false); // old status = false → new = true

        when(accountService.find(id)).thenReturn(account);
        when(accountService.updateStatusById(id, true)).thenReturn(false);

        String result = controller.updateStatus(id, redirectAttributes);
        String expectedRedirect = "redirect:/admin/account";
        String expectedMessage = "Thất bại...";
        String actualMessage = (String) redirectAttributes.getFlashAttributes().get("error");

        System.out.println("[TC003] Expected redirect: " + expectedRedirect + " | Actual: " + result);
        System.out.println("[TC003] Expected flash message: " + expectedMessage + " | Actual: " + actualMessage);
        System.out.println("[TC003] Expected new status: true | Actual: " + account.isStatus());

        assertEquals(expectedRedirect, result);
        assertEquals(expectedMessage, actualMessage);
        assertTrue(account.isStatus());
    }

    @Test
    void testcase_TC004_UpdateStatus_WhenExceptionOccurs_ShouldRedirectWithErrorMessage() {
        int id = 4;
        when(accountService.find(id)).thenThrow(new RuntimeException("Unexpected error"));

        String result = controller.updateStatus(id, redirectAttributes);
        String expectedRedirect = "redirect:/admin/account";
        String expectedMessage = "Thất bại...";
        String actualMessage = (String) redirectAttributes.getFlashAttributes().get("error");

        System.out.println("[TC004] Expected redirect: " + expectedRedirect + " | Actual: " + result);
        System.out.println("[TC004] Expected flash message: " + expectedMessage + " | Actual: " + actualMessage);

        assertEquals(expectedRedirect, result);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testcase_TC005_Index_WhenAccountListExists_ShouldReturnViewWithAccountList() {
        List<Account> mockAccounts = Arrays.asList(
                new Account(),
                new Account()
        );
        mockAccounts.get(0).setId(1);
        mockAccounts.get(0).setStatus(true);
        mockAccounts.get(0).setUsername("user1");
        mockAccounts.get(1).setId(2);
        mockAccounts.get(1).setStatus(true);
        mockAccounts.get(1).setUsername("user2");
        when(accountService.getAll()).thenReturn(mockAccounts);

        ModelMap model = new ModelMap();
        String result = controller.index(model);

        List<Account> resultList = (List<Account>) model.get("accountList");

        System.out.println("[TC005] Expected view: admin/account/index | Actual: " + result);
        System.out.println("[TC005] Expected account list size: 2 | Actual: " + resultList.size());

        assertEquals("admin/account/index", result);
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
    }

    @Test
    void testcase_TC006_Index_WhenAccountListEmpty_ShouldReturnViewWithEmptyList() {
        when(accountService.getAll()).thenReturn(Collections.emptyList());

        ModelMap model = new ModelMap();
        String result = controller.index(model);

        List<Account> resultList = (List<Account>) model.get("accountList");

        System.out.println("[TC006] Expected view: admin/account/index | Actual: " + result);
        System.out.println("[TC006] Expected account list size: 0 | Actual: " + resultList.size());

        assertEquals("admin/account/index", result);
        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    void testcase_TC007_Index_WhenServiceThrowsException_ShouldThrowRuntime() {
        when(accountService.getAll()).thenThrow(new RuntimeException("Something bad happened"));

        ModelMap model = new ModelMap();

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            controller.index(model);
        });

        System.out.println("[TC007] Exception message: " + thrown.getMessage());

        assertEquals("Something bad happened", thrown.getMessage());
    }

    @Test
    void testIndex_TC008_accountServiceThrowsException() {
        ModelMap model = new ModelMap();
        when(accountService.getAll()).thenThrow(new RuntimeException("accountService error"));

        assertThrows(RuntimeException.class, () -> controller.index(model));
    }

    @Test
    void testIndex_TC009_accountListWith2Accounts_ShouldReturnCorrectModelAndView() {
        List<Account> mockAccounts = Arrays.asList(
                new Account(),
                new Account()
        );
        mockAccounts.get(0).setId(1);
        mockAccounts.get(0).setStatus(true);
        mockAccounts.get(0).setUsername("user1");
        mockAccounts.get(1).setId(2);
        mockAccounts.get(1).setStatus(true);
        mockAccounts.get(1).setUsername("user2");
        when(accountService.getAll()).thenReturn(mockAccounts);

        ModelMap model = new ModelMap();
        String result = controller.index(model);

        List<Account> resultList = (List<Account>) model.get("accountList");

        System.out.println("[TC009] Expected view: admin/account/index | Actual: " + result);
        System.out.println("[TC009] Expected account list size: 2 | Actual: " + resultList.size());

        assertEquals("admin/account/index", result);
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
    }

    @Test
    void testIndex_TC010_emptyAccountList_ShouldReturnEmptyListInModel() {
        when(accountService.getAll()).thenReturn(Collections.emptyList());

        ModelMap model = new ModelMap();
        String result = controller.index(model);

        List<Account> resultList = (List<Account>) model.get("accountList");

        System.out.println("[TC010] Expected view: admin/account/index | Actual: " + result);
        System.out.println("[TC010] Expected account list size: 0 | Actual: " + resultList.size());

        assertEquals("admin/account/index", result);
        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    void testUpdateStatus_TC011_successfulUpdate_shouldReturnTrue() {
        int accountId = 1;
        boolean newStatus = true;

        when(accountService.updateStatusById(accountId, newStatus)).thenReturn(true);

        Boolean result = accountService.updateStatusById(accountId, newStatus);

        System.out.println("[TC011] Expected: true | Actual: " + result);
        assertTrue(result);
    }

    @Test
    void testUpdateStatus_TC012_failedUpdate_shouldReturnFalse() {
        int accountId = 2;
        boolean newStatus = false;

        when(accountService.updateStatusById(accountId, newStatus)).thenReturn(false);

        Boolean result = accountService.updateStatusById(accountId, newStatus);

        System.out.println("[TC012] Expected: false | Actual: " + result);
        assertFalse(result);
    }

    @Test
    void testFind_TC013_existingId_shouldReturnAccount() {
        int id = 1;
        Account mockAccount = new Account();
        mockAccount.setId(id);
        mockAccount.setUsername("mockUser");

        when(accountService.find(id)).thenReturn(mockAccount);

        Account result = accountService.find(id);

        System.out.println("[TC013] Expected username: mockUser | Actual: " + result.getUsername());
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("mockUser", result.getUsername());
    }

    @Test
    void testFind_TC014_nonExistingId_shouldThrowException() {
        int id = 2;

        when(accountService.find(id)).thenThrow(new NoSuchElementException("Account not found"));

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            accountService.find(id);
        });

        System.out.println("[TC014] Expected exception: NoSuchElementException | Actual: " + exception.getClass().getSimpleName());
        assertEquals("Account not found", exception.getMessage());
    }





}
