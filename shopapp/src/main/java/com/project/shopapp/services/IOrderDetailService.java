package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createorderDetail(OrderDetailDTO orderDetailDTO) throws Exception;
    OrderDetail getOrderDetail(Long id) throws Exception;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws Exception;
    void deleteById(Long id);
    List<OrderDetail> findByOrderId(Long orderId);
}
