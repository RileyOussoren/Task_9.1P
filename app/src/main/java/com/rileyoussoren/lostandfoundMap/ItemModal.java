package com.rileyoussoren.lostandfoundMap;

public class ItemModal {

    private String itemStatus;
    private String itemName;
    private String itemPhone;
    private String itemDescription;
    private String itemDate;
    private String itemLocation;
    private int id;

    public String getItemStatus() { return itemStatus; }

    public void setItemStatus(String itemStatus)
    {
        this.itemStatus = itemStatus;
    }

    public String getItemName() { return itemName; }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemPhone() { return itemPhone; }

    public void setItemPhone(String itemPhone)
    {
        this.itemPhone = itemPhone;
    }

    public String getItemDescription() { return itemDescription; }

    public void setItemDescription(String itemDescription)
    {
        this.itemDescription = itemDescription;
    }

    public String getItemDate() { return itemDate; }

    public void setItemDate(String itemDate)
    {
        this.itemDate = itemDate;
    }

    public String getItemLocation() { return itemLocation; }

    public void setItemLocation(String itemLocation)
    {
        this.itemLocation = itemLocation;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public ItemModal(String itemStatus,
                     String itemName,
                     String itemPhone,
                     String itemDescription,
                     String itemDate,
                     String itemLocation)
    {
        this.itemStatus = itemStatus;
        this.itemName = itemName;
        this.itemPhone = itemPhone;
        this.itemDescription = itemDescription;
        this.itemDate = itemDate;
        this.itemLocation = itemLocation;
    }


}
