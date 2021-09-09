package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArrayListProductDaoTest {
    @Mock
    private Product product1;
    @Mock
    private Product product2;
    @Mock
    private Product productToSave;
    @Spy
    private ArrayList<Product> products;

    @InjectMocks
    private ArrayListProductDao productDao = ArrayListProductDao.getInstance();

    @Before
    public void setup() {
        products.addAll(Arrays.asList(product1, product2));
        when(product1.getId()).thenReturn(1L);
        when(product1.getStock()).thenReturn(20);
        when(product1.getDescription()).thenReturn("Apple");
        when(product1.getPrice()).thenReturn(new BigDecimal(500));

        when(product2.getId()).thenReturn(2L);
        when(product2.getStock()).thenReturn(50);
        when(product2.getDescription()).thenReturn("Samsung");
        when(product2.getPrice()).thenReturn(new BigDecimal(300));
    }

    @Test
    public void testGetProduct() {
        assertEquals(product1.getId(), productDao.getProduct(1L).getId());
    }

    @Test(expected = ProductNotFoundException.class)
    public void testGetProductWithWrongId() {
        productDao.getProduct(0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetProductWithNullId() {
        productDao.getProduct(null);
    }

    @Test
    public void testFindProducts() {
        assertEquals(2, productDao.findProducts(null, null, null).size());
    }

    @Test
    public void testFindProductsWithShortQuery() {
        assertEquals(1, productDao.findProducts("Samsung", null, null).size());
    }

    @Test
    public void testFindProductsWithLongQuery() {
        assertEquals(2, productDao.findProducts("Samsung Apple", null, null).size());
    }

    @Test
    public void testFindProductsWithSorting() {
        assertEquals("Samsung", productDao.findProducts(null, "price", "asc").get(0).getDescription());
        assertEquals("Apple", productDao.findProducts(null, "price", "desc").get(0).getDescription());

        assertEquals("Apple", productDao.findProducts(null, "description", "asc").get(0).getDescription());
        assertEquals("Samsung", productDao.findProducts(null, "description", "desc").get(0).getDescription());
    }

    @Test
    public void testFindProductsWithDefaultSortOrder() {
        assertEquals("Samsung", productDao.findProducts(null, "price", null).get(0).getDescription());
        assertEquals("Apple", productDao.findProducts(null, "description", null).get(0).getDescription());
    }

    @Test
    public void testSaveProduct() {
        when(productToSave.getId()).thenReturn(3L);
        productDao.save(productToSave);
        assertEquals(3, products.size());
    }

    @Test
    public void testSaveEqualIdProducts() {
        when(productToSave.getId()).thenReturn(2L);
        productDao.save(productToSave);
        assertEquals(2, products.size());
    }

    @Test
    public void testSaveProductWithoutId() {
        productDao.save(productToSave);
        assertEquals(3, products.size());
    }

    @Test
    public void testDeleteProduct() {
        productDao.delete(1L);
        assertFalse(products.contains(product1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteProductWithNullId() {
        productDao.delete(null);
    }
}
