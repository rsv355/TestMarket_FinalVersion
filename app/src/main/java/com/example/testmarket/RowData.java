package com.example.testmarket;

import android.graphics.drawable.Drawable;

public class RowData {

	private String productId, description, adress, time, date, quantity,
			userId, endDate, endTime, region, isRedeem;

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getIsRedeem() {
		return isRedeem;
	}

	public void setIsRedeem(String isRedeem) {
		this.isRedeem = isRedeem;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	private Drawable drawable;

	public RowData(String productId, String description, String time,
			String date, String endDate, String endTime, String region,
			String adress, String quantity, String userId, String isRedeem,
			Drawable drawable) {

		this.productId = productId;
		this.description = description;
		this.time = time;
		this.adress = adress;
		this.date = date;
		this.endDate = endDate;
		this.endTime = endTime;
		this.region = region;
		this.quantity = quantity;
		this.userId = userId;
		this.isRedeem = isRedeem;
		this.drawable = drawable;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

}
