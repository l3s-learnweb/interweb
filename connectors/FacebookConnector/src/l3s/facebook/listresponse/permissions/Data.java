//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.03 at 04:40:37 PM CEST 
//


package l3s.facebook.listresponse.permissions;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}installed"/>
 *         &lt;element ref="{}read_stream"/>
 *         &lt;element ref="{}user_birthday"/>
 *         &lt;element ref="{}user_religion_politics"/>
 *         &lt;element ref="{}user_relationships"/>
 *         &lt;element ref="{}user_relationship_details"/>
 *         &lt;element ref="{}user_hometown"/>
 *         &lt;element ref="{}user_location"/>
 *         &lt;element ref="{}user_likes"/>
 *         &lt;element ref="{}user_activities"/>
 *         &lt;element ref="{}user_interests"/>
 *         &lt;element ref="{}user_education_history"/>
 *         &lt;element ref="{}user_work_history"/>
 *         &lt;element ref="{}user_online_presence"/>
 *         &lt;element ref="{}user_website"/>
 *         &lt;element ref="{}user_groups"/>
 *         &lt;element ref="{}user_events"/>
 *         &lt;element ref="{}user_photos"/>
 *         &lt;element ref="{}user_videos"/>
 *         &lt;element ref="{}user_photo_video_tags"/>
 *         &lt;element ref="{}user_notes"/>
 *         &lt;element ref="{}user_about_me"/>
 *         &lt;element ref="{}user_status"/>
 *         &lt;element ref="{}friends_birthday"/>
 *         &lt;element ref="{}friends_religion_politics"/>
 *         &lt;element ref="{}friends_relationships"/>
 *         &lt;element ref="{}friends_relationship_details"/>
 *         &lt;element ref="{}friends_hometown"/>
 *         &lt;element ref="{}friends_location"/>
 *         &lt;element ref="{}friends_likes"/>
 *         &lt;element ref="{}friends_activities"/>
 *         &lt;element ref="{}friends_interests"/>
 *         &lt;element ref="{}friends_education_history"/>
 *         &lt;element ref="{}friends_work_history"/>
 *         &lt;element ref="{}friends_online_presence"/>
 *         &lt;element ref="{}friends_website"/>
 *         &lt;element ref="{}friends_groups"/>
 *         &lt;element ref="{}friends_events"/>
 *         &lt;element ref="{}friends_photos"/>
 *         &lt;element ref="{}friends_videos"/>
 *         &lt;element ref="{}friends_photo_video_tags"/>
 *         &lt;element ref="{}friends_notes"/>
 *         &lt;element ref="{}friends_about_me"/>
 *         &lt;element ref="{}friends_status"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "installed",
    "readStream",
    "userBirthday",
    "userReligionPolitics",
    "userRelationships",
    "userRelationshipDetails",
    "userHometown",
    "userLocation",
    "userLikes",
    "userActivities",
    "userInterests",
    "userEducationHistory",
    "userWorkHistory",
    "userOnlinePresence",
    "userWebsite",
    "userGroups",
    "userEvents",
    "userPhotos",
    "userVideos",
    "userPhotoVideoTags",
    "userNotes",
    "userAboutMe",
    "userStatus",
    "friendsBirthday",
    "friendsReligionPolitics",
    "friendsRelationships",
    "friendsRelationshipDetails",
    "friendsHometown",
    "friendsLocation",
    "friendsLikes",
    "friendsActivities",
    "friendsInterests",
    "friendsEducationHistory",
    "friendsWorkHistory",
    "friendsOnlinePresence",
    "friendsWebsite",
    "friendsGroups",
    "friendsEvents",
    "friendsPhotos",
    "friendsVideos",
    "friendsPhotoVideoTags",
    "friendsNotes",
    "friendsAboutMe",
    "friendsStatus"
})
@XmlRootElement(name = "data")
public class Data {

    @XmlElement(required = true)
    protected BigInteger installed;
    @XmlElement(name = "read_stream", required = true)
    protected BigInteger readStream;
    @XmlElement(name = "user_birthday", required = true)
    protected BigInteger userBirthday;
    @XmlElement(name = "user_religion_politics", required = true)
    protected BigInteger userReligionPolitics;
    @XmlElement(name = "user_relationships", required = true)
    protected BigInteger userRelationships;
    @XmlElement(name = "user_relationship_details", required = true)
    protected BigInteger userRelationshipDetails;
    @XmlElement(name = "user_hometown", required = true)
    protected BigInteger userHometown;
    @XmlElement(name = "user_location", required = true)
    protected BigInteger userLocation;
    @XmlElement(name = "user_likes", required = true)
    protected BigInteger userLikes;
    @XmlElement(name = "user_activities", required = true)
    protected BigInteger userActivities;
    @XmlElement(name = "user_interests", required = true)
    protected BigInteger userInterests;
    @XmlElement(name = "user_education_history", required = true)
    protected BigInteger userEducationHistory;
    @XmlElement(name = "user_work_history", required = true)
    protected BigInteger userWorkHistory;
    @XmlElement(name = "user_online_presence", required = true)
    protected BigInteger userOnlinePresence;
    @XmlElement(name = "user_website", required = true)
    protected BigInteger userWebsite;
    @XmlElement(name = "user_groups", required = true)
    protected BigInteger userGroups;
    @XmlElement(name = "user_events", required = true)
    protected BigInteger userEvents;
    @XmlElement(name = "user_photos", required = true)
    protected BigInteger userPhotos;
    @XmlElement(name = "user_videos", required = true)
    protected BigInteger userVideos;
    @XmlElement(name = "user_photo_video_tags", required = true)
    protected BigInteger userPhotoVideoTags;
    @XmlElement(name = "user_notes", required = true)
    protected BigInteger userNotes;
    @XmlElement(name = "user_about_me", required = true)
    protected BigInteger userAboutMe;
    @XmlElement(name = "user_status", required = true)
    protected BigInteger userStatus;
    @XmlElement(name = "friends_birthday", required = true)
    protected BigInteger friendsBirthday;
    @XmlElement(name = "friends_religion_politics", required = true)
    protected BigInteger friendsReligionPolitics;
    @XmlElement(name = "friends_relationships", required = true)
    protected BigInteger friendsRelationships;
    @XmlElement(name = "friends_relationship_details", required = true)
    protected BigInteger friendsRelationshipDetails;
    @XmlElement(name = "friends_hometown", required = true)
    protected BigInteger friendsHometown;
    @XmlElement(name = "friends_location", required = true)
    protected BigInteger friendsLocation;
    @XmlElement(name = "friends_likes", required = true)
    protected BigInteger friendsLikes;
    @XmlElement(name = "friends_activities", required = true)
    protected BigInteger friendsActivities;
    @XmlElement(name = "friends_interests", required = true)
    protected BigInteger friendsInterests;
    @XmlElement(name = "friends_education_history", required = true)
    protected BigInteger friendsEducationHistory;
    @XmlElement(name = "friends_work_history", required = true)
    protected BigInteger friendsWorkHistory;
    @XmlElement(name = "friends_online_presence", required = true)
    protected BigInteger friendsOnlinePresence;
    @XmlElement(name = "friends_website", required = true)
    protected BigInteger friendsWebsite;
    @XmlElement(name = "friends_groups", required = true)
    protected BigInteger friendsGroups;
    @XmlElement(name = "friends_events", required = true)
    protected BigInteger friendsEvents;
    @XmlElement(name = "friends_photos", required = true)
    protected BigInteger friendsPhotos;
    @XmlElement(name = "friends_videos", required = true)
    protected BigInteger friendsVideos;
    @XmlElement(name = "friends_photo_video_tags", required = true)
    protected BigInteger friendsPhotoVideoTags;
    @XmlElement(name = "friends_notes", required = true)
    protected BigInteger friendsNotes;
    @XmlElement(name = "friends_about_me", required = true)
    protected BigInteger friendsAboutMe;
    @XmlElement(name = "friends_status", required = true)
    protected BigInteger friendsStatus;

    /**
     * Gets the value of the installed property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getInstalled() {
        return installed;
    }

    /**
     * Sets the value of the installed property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setInstalled(BigInteger value) {
        this.installed = value;
    }

    /**
     * Gets the value of the readStream property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getReadStream() {
        return readStream;
    }

    /**
     * Sets the value of the readStream property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setReadStream(BigInteger value) {
        this.readStream = value;
    }

    /**
     * Gets the value of the userBirthday property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserBirthday() {
        return userBirthday;
    }

    /**
     * Sets the value of the userBirthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserBirthday(BigInteger value) {
        this.userBirthday = value;
    }

    /**
     * Gets the value of the userReligionPolitics property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserReligionPolitics() {
        return userReligionPolitics;
    }

    /**
     * Sets the value of the userReligionPolitics property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserReligionPolitics(BigInteger value) {
        this.userReligionPolitics = value;
    }

    /**
     * Gets the value of the userRelationships property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserRelationships() {
        return userRelationships;
    }

    /**
     * Sets the value of the userRelationships property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserRelationships(BigInteger value) {
        this.userRelationships = value;
    }

    /**
     * Gets the value of the userRelationshipDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserRelationshipDetails() {
        return userRelationshipDetails;
    }

    /**
     * Sets the value of the userRelationshipDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserRelationshipDetails(BigInteger value) {
        this.userRelationshipDetails = value;
    }

    /**
     * Gets the value of the userHometown property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserHometown() {
        return userHometown;
    }

    /**
     * Sets the value of the userHometown property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserHometown(BigInteger value) {
        this.userHometown = value;
    }

    /**
     * Gets the value of the userLocation property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserLocation() {
        return userLocation;
    }

    /**
     * Sets the value of the userLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserLocation(BigInteger value) {
        this.userLocation = value;
    }

    /**
     * Gets the value of the userLikes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserLikes() {
        return userLikes;
    }

    /**
     * Sets the value of the userLikes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserLikes(BigInteger value) {
        this.userLikes = value;
    }

    /**
     * Gets the value of the userActivities property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserActivities() {
        return userActivities;
    }

    /**
     * Sets the value of the userActivities property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserActivities(BigInteger value) {
        this.userActivities = value;
    }

    /**
     * Gets the value of the userInterests property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserInterests() {
        return userInterests;
    }

    /**
     * Sets the value of the userInterests property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserInterests(BigInteger value) {
        this.userInterests = value;
    }

    /**
     * Gets the value of the userEducationHistory property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserEducationHistory() {
        return userEducationHistory;
    }

    /**
     * Sets the value of the userEducationHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserEducationHistory(BigInteger value) {
        this.userEducationHistory = value;
    }

    /**
     * Gets the value of the userWorkHistory property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserWorkHistory() {
        return userWorkHistory;
    }

    /**
     * Sets the value of the userWorkHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserWorkHistory(BigInteger value) {
        this.userWorkHistory = value;
    }

    /**
     * Gets the value of the userOnlinePresence property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserOnlinePresence() {
        return userOnlinePresence;
    }

    /**
     * Sets the value of the userOnlinePresence property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserOnlinePresence(BigInteger value) {
        this.userOnlinePresence = value;
    }

    /**
     * Gets the value of the userWebsite property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserWebsite() {
        return userWebsite;
    }

    /**
     * Sets the value of the userWebsite property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserWebsite(BigInteger value) {
        this.userWebsite = value;
    }

    /**
     * Gets the value of the userGroups property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserGroups() {
        return userGroups;
    }

    /**
     * Sets the value of the userGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserGroups(BigInteger value) {
        this.userGroups = value;
    }

    /**
     * Gets the value of the userEvents property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserEvents() {
        return userEvents;
    }

    /**
     * Sets the value of the userEvents property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserEvents(BigInteger value) {
        this.userEvents = value;
    }

    /**
     * Gets the value of the userPhotos property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserPhotos() {
        return userPhotos;
    }

    /**
     * Sets the value of the userPhotos property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserPhotos(BigInteger value) {
        this.userPhotos = value;
    }

    /**
     * Gets the value of the userVideos property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserVideos() {
        return userVideos;
    }

    /**
     * Sets the value of the userVideos property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserVideos(BigInteger value) {
        this.userVideos = value;
    }

    /**
     * Gets the value of the userPhotoVideoTags property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserPhotoVideoTags() {
        return userPhotoVideoTags;
    }

    /**
     * Sets the value of the userPhotoVideoTags property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserPhotoVideoTags(BigInteger value) {
        this.userPhotoVideoTags = value;
    }

    /**
     * Gets the value of the userNotes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserNotes() {
        return userNotes;
    }

    /**
     * Sets the value of the userNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserNotes(BigInteger value) {
        this.userNotes = value;
    }

    /**
     * Gets the value of the userAboutMe property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserAboutMe() {
        return userAboutMe;
    }

    /**
     * Sets the value of the userAboutMe property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserAboutMe(BigInteger value) {
        this.userAboutMe = value;
    }

    /**
     * Gets the value of the userStatus property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserStatus() {
        return userStatus;
    }

    /**
     * Sets the value of the userStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserStatus(BigInteger value) {
        this.userStatus = value;
    }

    /**
     * Gets the value of the friendsBirthday property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsBirthday() {
        return friendsBirthday;
    }

    /**
     * Sets the value of the friendsBirthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsBirthday(BigInteger value) {
        this.friendsBirthday = value;
    }

    /**
     * Gets the value of the friendsReligionPolitics property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsReligionPolitics() {
        return friendsReligionPolitics;
    }

    /**
     * Sets the value of the friendsReligionPolitics property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsReligionPolitics(BigInteger value) {
        this.friendsReligionPolitics = value;
    }

    /**
     * Gets the value of the friendsRelationships property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsRelationships() {
        return friendsRelationships;
    }

    /**
     * Sets the value of the friendsRelationships property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsRelationships(BigInteger value) {
        this.friendsRelationships = value;
    }

    /**
     * Gets the value of the friendsRelationshipDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsRelationshipDetails() {
        return friendsRelationshipDetails;
    }

    /**
     * Sets the value of the friendsRelationshipDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsRelationshipDetails(BigInteger value) {
        this.friendsRelationshipDetails = value;
    }

    /**
     * Gets the value of the friendsHometown property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsHometown() {
        return friendsHometown;
    }

    /**
     * Sets the value of the friendsHometown property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsHometown(BigInteger value) {
        this.friendsHometown = value;
    }

    /**
     * Gets the value of the friendsLocation property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsLocation() {
        return friendsLocation;
    }

    /**
     * Sets the value of the friendsLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsLocation(BigInteger value) {
        this.friendsLocation = value;
    }

    /**
     * Gets the value of the friendsLikes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsLikes() {
        return friendsLikes;
    }

    /**
     * Sets the value of the friendsLikes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsLikes(BigInteger value) {
        this.friendsLikes = value;
    }

    /**
     * Gets the value of the friendsActivities property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsActivities() {
        return friendsActivities;
    }

    /**
     * Sets the value of the friendsActivities property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsActivities(BigInteger value) {
        this.friendsActivities = value;
    }

    /**
     * Gets the value of the friendsInterests property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsInterests() {
        return friendsInterests;
    }

    /**
     * Sets the value of the friendsInterests property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsInterests(BigInteger value) {
        this.friendsInterests = value;
    }

    /**
     * Gets the value of the friendsEducationHistory property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsEducationHistory() {
        return friendsEducationHistory;
    }

    /**
     * Sets the value of the friendsEducationHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsEducationHistory(BigInteger value) {
        this.friendsEducationHistory = value;
    }

    /**
     * Gets the value of the friendsWorkHistory property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsWorkHistory() {
        return friendsWorkHistory;
    }

    /**
     * Sets the value of the friendsWorkHistory property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsWorkHistory(BigInteger value) {
        this.friendsWorkHistory = value;
    }

    /**
     * Gets the value of the friendsOnlinePresence property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsOnlinePresence() {
        return friendsOnlinePresence;
    }

    /**
     * Sets the value of the friendsOnlinePresence property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsOnlinePresence(BigInteger value) {
        this.friendsOnlinePresence = value;
    }

    /**
     * Gets the value of the friendsWebsite property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsWebsite() {
        return friendsWebsite;
    }

    /**
     * Sets the value of the friendsWebsite property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsWebsite(BigInteger value) {
        this.friendsWebsite = value;
    }

    /**
     * Gets the value of the friendsGroups property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsGroups() {
        return friendsGroups;
    }

    /**
     * Sets the value of the friendsGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsGroups(BigInteger value) {
        this.friendsGroups = value;
    }

    /**
     * Gets the value of the friendsEvents property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsEvents() {
        return friendsEvents;
    }

    /**
     * Sets the value of the friendsEvents property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsEvents(BigInteger value) {
        this.friendsEvents = value;
    }

    /**
     * Gets the value of the friendsPhotos property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsPhotos() {
        return friendsPhotos;
    }

    /**
     * Sets the value of the friendsPhotos property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsPhotos(BigInteger value) {
        this.friendsPhotos = value;
    }

    /**
     * Gets the value of the friendsVideos property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsVideos() {
        return friendsVideos;
    }

    /**
     * Sets the value of the friendsVideos property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsVideos(BigInteger value) {
        this.friendsVideos = value;
    }

    /**
     * Gets the value of the friendsPhotoVideoTags property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsPhotoVideoTags() {
        return friendsPhotoVideoTags;
    }

    /**
     * Sets the value of the friendsPhotoVideoTags property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsPhotoVideoTags(BigInteger value) {
        this.friendsPhotoVideoTags = value;
    }

    /**
     * Gets the value of the friendsNotes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsNotes() {
        return friendsNotes;
    }

    /**
     * Sets the value of the friendsNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsNotes(BigInteger value) {
        this.friendsNotes = value;
    }

    /**
     * Gets the value of the friendsAboutMe property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsAboutMe() {
        return friendsAboutMe;
    }

    /**
     * Sets the value of the friendsAboutMe property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsAboutMe(BigInteger value) {
        this.friendsAboutMe = value;
    }

    /**
     * Gets the value of the friendsStatus property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFriendsStatus() {
        return friendsStatus;
    }

    /**
     * Sets the value of the friendsStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFriendsStatus(BigInteger value) {
        this.friendsStatus = value;
    }

}