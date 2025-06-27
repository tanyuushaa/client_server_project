package com.github.tanyuushaa.server.impl;

import com.github.tanyuushaa.model.Group;
import com.github.tanyuushaa.model.Product;
import com.github.tanyuushaa.model.Response;
import com.github.tanyuushaa.model.rep.GroupRep;
import com.github.tanyuushaa.server.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupServiceImplTest {

    @Mock
    private GroupRep groupRep;

    @Mock
    private ProductService productService;

    @InjectMocks
    private GroupServiceImpl groupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveGroupWithDuplicateNameReturnsError() {
        Group existingGroup = new Group("Bakery", "Baked products");
        existingGroup.setId(1L);

        when(groupRep.findGroupByName("Bakery")).thenReturn(existingGroup);

        Group newGroup = new Group("Bakery", "Some other description");

        Response<Group> response = groupService.save(newGroup);

        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("Group with name Bakery already exists"));
        verify(groupRep, never()).save(any(Group.class));
    }

    @Test
    void testDeleteGroupAlsoDeletesProducts() {
        Group group = new Group("TestGroup", "Description");
        Product product = new Product("TestProduct", group, "Producer", 10.0, 5, "Description");

        when(productService.findAllByGroup(group)).thenReturn(Collections.singletonList(product));

        groupService.delete(group);

        verify(productService).delete(Collections.singletonList(product));
        verify(groupRep).delete(group);
    }

    @Test
    void testSearchByKeywordFound() {
        Group g = new Group("Bakery", "Fresh breads");
        when(groupRep.findAll()).thenReturn(Collections.singletonList(g));

        List<Group> result = groupService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bakery", result.get(0).getName());
    }

    @Test
    void testSearchByKeywordEmpty() {
        when(groupRep.findAll()).thenReturn(Collections.emptyList());

        List<Group> result = groupService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

