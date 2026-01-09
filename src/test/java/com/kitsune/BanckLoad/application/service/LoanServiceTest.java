package com.kitsune.BanckLoad.application.service;

import com.kitsune.BanckLoad.application.dto.LoanRequestDTO;
import com.kitsune.BanckLoad.application.dto.LoanResponseDTO;
import com.kitsune.BanckLoad.application.dto.LoanReviewDTO;
import com.kitsune.BanckLoad.domain.model.Loan;
import com.kitsune.BanckLoad.domain.model.User;
import com.kitsune.BanckLoad.infrastructure.repository.LoanRepository;
import com.kitsune.BanckLoad.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoanService loanService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .password("password")
                .fullName("Test User")
                .roles(Set.of("USER"))
                .enabled(true)
                .build();

        adminUser = User.builder()
                .id(2L)
                .username("admin")
                .email("admin@test.com")
                .password("password")
                .fullName("Admin User")
                .roles(Set.of("ADMIN", "USER"))
                .enabled(true)
                .build();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testRequestLoan_Success() {
        // Arrange
        LoanRequestDTO request = LoanRequestDTO.builder()
                .amount(new BigDecimal("5000.00"))
                .term(12)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Loan savedLoan = Loan.builder()
                .id(1L)
                .amount(request.getAmount())
                .term(request.getTerm())
                .user(testUser)
                .status(Loan.LoanStatus.PENDING)
                .build();

        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        // Act
        LoanResponseDTO response = loanService.requestLoan(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("5000.00"), response.getAmount());
        assertEquals(12, response.getTerm());
        assertEquals("PENDING", response.getStatus());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testRequestLoan_UserNotFound() {
        // Arrange
        LoanRequestDTO request = LoanRequestDTO.builder()
                .amount(new BigDecimal("5000.00"))
                .term(12)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent");
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanService.requestLoan(request));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void testGetUserLoans_Success() {
        // Arrange
        Loan loan1 = Loan.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .term(12)
                .user(testUser)
                .status(Loan.LoanStatus.PENDING)
                .build();

        Loan loan2 = Loan.builder()
                .id(2L)
                .amount(new BigDecimal("10000.00"))
                .term(24)
                .user(testUser)
                .status(Loan.LoanStatus.APPROVED)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(loanRepository.findByUser(testUser)).thenReturn(Arrays.asList(loan1, loan2));

        // Act
        List<LoanResponseDTO> loans = loanService.getUserLoans("testuser");

        // Assert
        assertNotNull(loans);
        assertEquals(2, loans.size());
        assertEquals("PENDING", loans.get(0).getStatus());
        assertEquals("APPROVED", loans.get(1).getStatus());
    }

    @Test
    void testReviewLoan_Approve_Success() {
        // Arrange
        Loan pendingLoan = Loan.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .term(12)
                .user(testUser)
                .status(Loan.LoanStatus.PENDING)
                .build();

        LoanReviewDTO reviewDTO = LoanReviewDTO.builder()
                .action("APPROVED")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(pendingLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(pendingLoan);

        // Act
        LoanResponseDTO response = loanService.reviewLoan(1L, reviewDTO);

        // Assert
        assertNotNull(response);
        assertEquals("APPROVED", response.getStatus());
        assertNull(response.getRejectionReason());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testReviewLoan_Reject_Success() {
        // Arrange
        Loan pendingLoan = Loan.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .term(12)
                .user(testUser)
                .status(Loan.LoanStatus.PENDING)
                .build();

        LoanReviewDTO reviewDTO = LoanReviewDTO.builder()
                .action("REJECTED")
                .rejectionReason("Monto muy alto")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(pendingLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(pendingLoan);

        // Act
        LoanResponseDTO response = loanService.reviewLoan(1L, reviewDTO);

        // Assert
        assertNotNull(response);
        assertEquals("REJECTED", response.getStatus());
        assertEquals("Monto muy alto", response.getRejectionReason());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testReviewLoan_AlreadyReviewed() {
        // Arrange
        Loan approvedLoan = Loan.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .term(12)
                .user(testUser)
                .status(Loan.LoanStatus.APPROVED)
                .build();

        LoanReviewDTO reviewDTO = LoanReviewDTO.builder()
                .action("APPROVED")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(approvedLoan));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanService.reviewLoan(1L, reviewDTO));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void testReviewLoan_LoanNotFound() {
        // Arrange
        LoanReviewDTO reviewDTO = LoanReviewDTO.builder()
                .action("APPROVED")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanService.reviewLoan(999L, reviewDTO));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void testGetPendingLoans_Success() {
        // Arrange
        Loan loan1 = Loan.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .term(12)
                .user(testUser)
                .status(Loan.LoanStatus.PENDING)
                .build();

        when(loanRepository.findByStatus(Loan.LoanStatus.PENDING))
                .thenReturn(Arrays.asList(loan1));

        // Act
        List<LoanResponseDTO> loans = loanService.getPendingLoans();

        // Assert
        assertNotNull(loans);
        assertEquals(1, loans.size());
        assertEquals("PENDING", loans.get(0).getStatus());
    }
}

