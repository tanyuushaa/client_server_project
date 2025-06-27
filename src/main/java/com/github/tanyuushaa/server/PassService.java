package com.github.tanyuushaa.server;

import com.github.tanyuushaa.model.Product;
import com.github.tanyuushaa.model.Response;
import org.springframework.stereotype.Service;


public interface PassService {

    String encodePassword(String password);
    boolean comparePasswordAndConfirmationPassword(String password, String confirmationPassword);
    boolean compareRawAndEncodedPassword(String raw, String encoded);
//    Response<Product> receiveProduct(long id, long amount);
//    Response<Product> consumeProduct(long id, long amount);

}
