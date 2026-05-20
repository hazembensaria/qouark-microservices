package com.infotexa.storageservice.query;

public class StorageQuery {

    public static final String CREATE_STORAGE_FOLDER =
            """
                SELECT * FROM create_storage_folder(:userUuid,:parentUuid,:folderUuid,:name);
            """;

    public static final String GET_STORAGE_FOLDERS =
            """
            SELECT * FROM get_storage_folders(:folderUuid) ;
            """;
    public static final String GET_FOLDER_BY_UUID =
            """
            SELECT * FROM storage_folders WHERE storage_folder_uuid = :folderUuid ;
            """;
    public static final String SAVE_STORAGE_FILE =
            """
            SELECT * FROM save_storage_file(:fileUuid,:userUuid,:folderUuid,:name,:extension,:size,:formattedSize,:storagePath,:uri) ;
            """;
    public static final String GET_STORAGE_FILES =
            """
            SELECT * FROM get_storage_files(:folderUuid) ;
            """;
    public static final String DELETE_STORAGE_FILE =
            """
            SELECT * FROM delete_storage_file(:fileUuid) ;
            """;
    public static final String CREATE_PUBLIC_LINK =
            """
            SELECT token FROM create_public_link(:publicLinkUuid,:fileUuid,:token,:userUuid) ;
            """;
    public static final String CHECK_QUOTA =
            """
            SELECT (used_size_bytes + :fileSize) <= max_size_bytes FROM user_storage_quotas uq JOIN users u ON u.user_id = uq.user_id WHERE u.user_uuid = :userUuid ;
            """;
    public static final String GET_ROOT_FOLDER =
            """
            SELECT * FROM storage_folders WHERE owner_id = ( SELECT user_id FROM users WHERE user_uuid = :userUuid) AND is_root = true ;
            """;
    public static final String SELECT_FILES_BY_USER_UUID_QUERY =
            """
            SELECT * FROM storage_files WHERE storage_file_uuid = :fileUuid ;
            """;
    public static final String SHARE_FOLDER =
            """
            SELECT * FROM share_storage_folder(:ownerUuid,:folderUuid,:sharedWithUserUuid,:permission) ;
            """;
    public static final String SHARE_FILE =
            """
            SELECT share_storage_file(:ownerUuid,:fileUuid,:sharedWithUserUuid,:permission) ;
            """;
}