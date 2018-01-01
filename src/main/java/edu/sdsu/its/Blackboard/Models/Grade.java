package edu.sdsu.its.Blackboard.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Data
@Builder
public class Grade {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("columnId")
    @Expose
    private String columnId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("displayGrade")
    @Expose
    private DisplayGrade displayGrade;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("score")
    @Expose
    private Double score;
    @SerializedName("overridden")
    @Expose
    private String overridden;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("exempt")
    @Expose
    private Boolean exempt;
    @SerializedName("corrupt")
    @Expose
    private Boolean corrupt;

}
