package com.nicksbbq.nicksbarbecue;

/**
 * Created by Work on 9/19/2016.
 */
public class Coupon {
    public String couponDescription, couponExpiration, couponImage;

    public Coupon() {

    }

    public Coupon(String couponDescription, String couponExpiration, String couponImage) {
        this.couponDescription = couponDescription;
        this.couponExpiration = couponExpiration;
        this.couponImage = couponImage;
    }
}
