package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;




import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository mockRoleRepository;

    private RoleServiceImpl serviceToTest;

    @BeforeEach
    void setUp() {
        serviceToTest = new RoleServiceImpl(mockRoleRepository);
    }

    @Test
    void testSeedRoles() {
        when(mockRoleRepository.count()).thenReturn(0L);
        serviceToTest.seedRoles();
        verify(mockRoleRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testFindByName() {
        RoleName validRoleName = RoleName.USER;
        RoleName invalidRoleName = null;
        Role role = new Role(validRoleName);
        when(mockRoleRepository.findByName(validRoleName)).thenReturn(role);
       assertEquals(role, serviceToTest.findByName(validRoleName));

    }



}