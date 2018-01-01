package edu.sdsu.its.Blackboard.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Data
public class Course {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("externalId")
    @Expose
    private String externalId;
    @SerializedName("dataSourceId")
    @Expose
    private String dataSourceId;
    @SerializedName("courseId")
    @Expose
    private String courseId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("organization")
    @Expose
    private Boolean organization;
    @SerializedName("ultraStatus")
    @Expose
    private String ultraStatus;
    @SerializedName("allowGuests")
    @Expose
    private Boolean allowGuests;
    @SerializedName("readOnly")
    @Expose
    private Boolean readOnly;
    @SerializedName("termId")
    @Expose
    private String termId;
}
