package edu.sdsu.its.Blackboard.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DisplayGrade {

    @SerializedName("scaleType")
    @Expose
    private String scaleType;
    @SerializedName("score")
    @Expose
    private Integer score;
    @SerializedName("text")
    @Expose
    private String text;

}