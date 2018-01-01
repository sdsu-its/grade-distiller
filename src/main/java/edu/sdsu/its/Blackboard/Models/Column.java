package edu.sdsu.its.Blackboard.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Data
public class Column {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("externalId")
    @Expose
    private String externalId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("externalGrade")
    @Expose
    private Boolean externalGrade;
    @SerializedName("created")
    @Expose
    private String created;
}
