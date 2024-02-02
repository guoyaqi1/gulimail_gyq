package com.atguigu.gulimail.cart.controller;

import com.atguigu.gulimail.cart.interceptor.CartInterceptor;
import com.atguigu.gulimail.cart.service.CartService;
import com.atguigu.gulimail.cart.vo.CartItemVo;
import com.atguigu.gulimail.cart.vo.UserInfoTo;
import com.atguigu.gulimail.common.constant.AuthServerConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author:guoyaqi
 * @Date: 2023/9/1 19:21
 */
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/currentUserCartItems")
    public List<CartItemVo> getCurrentUserCartItems(){
        return cartService.getUserCartItems();
    }

    /**
     * 删除购物车选项
     * @param skuId
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimail.com/cart.html";
    }

    /**
     * 修改购物车数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("countItem")
    public String countItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num){
        cartService.changeItemCount(skuId,num);

        return "redirect:http://cart.gulimail.com/cart.html";
    }

    /**
     * 选中购物车
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);

        return "redirect:http://cart.gulimail.com/cart.html";
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        System.out.println(userInfoTo);
        /*if(attribute==null){
            //没登录的获取临时购物车数据
        }else {
            //获取登录了的购物车

        }*/
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num")  Integer num,
                            RedirectAttributes attributes) throws ExecutionException, InterruptedException{
        cartService.addToCart(skuId,num);

        attributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimail.com/addToCartSuccessPage.html";
    }

    /**
     * 跳转到添加购物车成功页面
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping(value = "/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,
                                       Model model) {
        //重定向到成功页面。再次查询购物车数据即可
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItemVo);
        return "success";
    }


}
