package com.mobileshop.group8.controller;

import com.mobileshop.group8.common.Constants;
import com.mobileshop.group8.model.Product;
import com.mobileshop.group8.model.cart.Cart;
import com.mobileshop.group8.model.cart.CartBean;
import com.mobileshop.group8.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class BaseController {
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/")
    public ModelAndView index() {
        List<Product> tblProductsList = this.productService.findAll();
        return new ModelAndView(Constants.PRODUCT_LIST_URL, "products", tblProductsList);
    }

    @RequestMapping(value = "/view", params = {"id"}, method = RequestMethod.GET)
    public ModelAndView searchProduct(@RequestParam("id") Integer productId) {
        Product product = this.productService.findByProductId(productId);
        if (product != null) {
            return new ModelAndView(Constants.PRODUCT_DETAIL_URL, "product", product);
        }
        return index();
    }

    @RequestMapping(value = "/addtocart", params = {"id"}, method = RequestMethod.GET)
    public ModelAndView addToCart(@RequestParam("id") Integer productId, HttpSession session) {
        CartBean carlist = (CartBean) session.getAttribute(Constants.ATTR_CART);
        if (carlist == null){
            carlist = new CartBean();
        }
        Product product = this.productService.findByProductId(productId);
        if (product != null) {
            Cart cart = new Cart(product,1);
            carlist.addProduct(cart);
            session.setAttribute(Constants.ATTR_CART,carlist);
            return new ModelAndView(Constants.VIEW_CART_URL);
        }
        return new ModelAndView(Constants.HOME_URL);

    }

    @RequestMapping(value = "/removecart", method = RequestMethod.GET)
    public RedirectView removeCart(@RequestParam("id") Integer productId, HttpSession session) {
        CartBean carlist = (CartBean) session.getAttribute(Constants.ATTR_CART);
        if (carlist != null){
            Product product = this.productService.findByProductId(productId);
            if (product != null) {
                carlist.removeProduct(productId);
                session.setAttribute(Constants.ATTR_CART,carlist);
            }
        }
        return new RedirectView("cart");

    }


    @RequestMapping(value ="/cart")
    public ModelAndView viewCart(HttpSession session){
        return new ModelAndView(Constants.VIEW_CART_URL);
    }

}
