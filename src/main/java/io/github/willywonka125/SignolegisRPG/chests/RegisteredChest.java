package io.github.willywonka125.SignolegisRPG.chests;

/**
 * In-memory representation of one registered chest and its settings, loaded from data.yml.
 * Every setting has a default that matches the original chest behaviour (shared contents,
 * restored on every open), so a chest with no attributes in data.yml behaves as before.
 * Setters are package-private: only classes in the chests package (e.g. the loader) can change settings.
 */
public class RegisteredChest {

	// Default enum values
	private AccessMode accessMode = AccessMode.ANYONE;
	private Contents contents = Contents.SHARED;
	private InteractionMode interactionMode = InteractionMode.TAKE_AND_PUT;
	private RefillTrigger refillTrigger = RefillTrigger.ON_OPEN;
	private TimerResetMode timerResetMode = TimerResetMode.NEVER;

	//Declarations
	private String name;
	private final String key;
	private boolean protection = true;
	private String groupAccessName = "admin";
	private String permissionAccessName = "signolegis.admin";
	private int refillCount = -1;
	private int refillTimer = 3600;
	private String questId;

	/**
	 * Creates a chest with default settings.
	 * @param chestKey The chest's key in data.yml (world,x,y,z). Cannot be changed later.
	 * @param chestName The chest's display name. Does not have to be unique.
	 */
	public RegisteredChest (String chestKey, String chestName) {
		this.key = chestKey;
		setName(chestName);
	}


	/**
	 * @return The chest's display name.
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name New display name for the chest.
	 */
	void setName(String name) {
		this.name = name;
	}


	/**
	 * @return The chest's key in data.yml (world,x,y,z).
	 */
	public String getKey() {
		return key;
	}


	/**
	 * @return true if the chest is protected from being destroyed while registered. Default true.
	 */
	public boolean isProtected() {
		return protection;
	}


	/**
	 * @param protection true to protect the chest from being destroyed while registered.
	 */
	void setProtection(boolean protection) {
		this.protection = protection;
	}


	/**
	 * @return Name of the group allowed to open the chest. Only used when access mode is GROUP; null if not set.
	 */
	public String getGroupAccessName() {
		return groupAccessName;
	}


	/**
	 * @param groupAccessName Name of the group allowed to open the chest (used with access mode GROUP).
	 */
	void setGroupAccessName(String groupAccessName) {
		this.groupAccessName = groupAccessName;
	}


	/**
	 * @return Permission required to open the chest. Only used when access mode is PERMISSION; null if not set.
	 */
	public String getPermissionAccessName() {
		return permissionAccessName;
	}


	/**
	 * @param permissionAccessName Permission required to open the chest (used with access mode PERMISSION).
	 */
	void setPermissionAccessName(String permissionAccessName) {
		this.permissionAccessName = permissionAccessName;
	}


	/**
	 * @return How many times the chest can be refilled; -1 means unlimited. Default -1.
	 */
	public int getRefillCount() {
		return refillCount;
	}


	/**
	 * @param refillCount How many times the chest can be refilled; -1 for unlimited.
	 */
	void setRefillCount(int refillCount) {
		this.refillCount = refillCount;
	}


	/**
	 * @return Refill interval in seconds. Only used when the refill trigger is TIMER. Default 3600.
	 */
	public int getRefillTimer() {
		return refillTimer;
	}


	/**
	 * @param refillTimer Refill interval in seconds (used with refill trigger TIMER).
	 */
	void setRefillTimer(int refillTimer) {
		this.refillTimer = refillTimer;
	}


	/**
	 * @return ID of the quest that refills the chest. Only used when the refill trigger is QUEST; null if not set.
	 */
	public String getQuestId() {
		return questId;
	}


	/**
	 * @param questId ID of the quest that refills the chest (used with refill trigger QUEST).
	 */
	void setQuestId(String questId) {
		this.questId = questId;
	}


	/**
	 * @return Who may open the chest. Default ANYONE.
	 */
	public AccessMode getAccessMode() {
		return accessMode;
	}


	/**
	 * @param accessMode Who may open the chest.
	 */
	void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}


	/**
	 * @return Whether contents are shared or tracked per player / per group. Default SHARED.
	 */
	public Contents getContents() {
		return contents;
	}


	/**
	 * @param contents Whether contents are shared or tracked per player / per group.
	 */
	void setContents(Contents contents) {
		this.contents = contents;
	}


	/**
	 * @return What players may do with the items inside (take, put, both, or only look). Default TAKE_AND_PUT.
	 */
	public InteractionMode getInteractionMode() {
		return interactionMode;
	}


	/**
	 * @param interactionMode What players may do with the items inside.
	 */
	void setInteractionMode(InteractionMode interactionMode) {
		this.interactionMode = interactionMode;
	}


	/**
	 * @return What causes the chest to refill. Default ON_OPEN (original behaviour).
	 */
	public RefillTrigger getRefillTrigger() {
		return refillTrigger;
	}


	/**
	 * @param refillTrigger What causes the chest to refill.
	 */
	void setRefillTrigger(RefillTrigger refillTrigger) {
		this.refillTrigger = refillTrigger;
	}


	/**
	 * @return How the refill timer is reset. Only used when the refill trigger is TIMER. Default NEVER.
	 */
	public TimerResetMode getTimerResetMode() {
		return timerResetMode;
	}


	/**
	 * @param timerResetMode How the refill timer is reset (used with refill trigger TIMER).
	 */
	void setTimerResetMode(TimerResetMode timerResetMode) {
		this.timerResetMode = timerResetMode;
	}

}
