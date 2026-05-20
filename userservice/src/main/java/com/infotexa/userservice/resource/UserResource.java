package com.infotexa.userservice.resource;


import com.infotexa.userservice.domain.Response;
import com.infotexa.userservice.dtoRequest.PasswordRequest;
import com.infotexa.userservice.dtoRequest.ResetPasswordRequest;
import com.infotexa.userservice.dtoRequest.RoleRequest;
import com.infotexa.userservice.dtoRequest.UserRequest;
import com.infotexa.userservice.model.User;
import com.infotexa.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static com.infotexa.userservice.consatant.Constant.PHOTO_DIRECTORY;
import static com.infotexa.userservice.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;
import static org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserResource {
        private UserService userService;

        @PostMapping("/register")
        public ResponseEntity<Response> register(@RequestBody UserRequest user , HttpServletRequest request) {
          userService.createUser(user.getFirstName() , user.getLastName() , user.getEmail(), user.getUsername() , user.getPassword());
            return created(getUri()).body(getResponse(request , emptyMap() , "Account created. check your email to enable your account", CREATED));
        }

    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("token") String token , HttpServletRequest request) {
        userService.verifyAccount(token);
        return ok(getResponse(request , emptyMap() , "Account verified . You may login now", OK));
    }

    @PostMapping("/organization/create")
    public ResponseEntity<Response> createOrganization(
            @NotNull Authentication authentication,
            HttpServletRequest request,
            @RequestParam(value = "name", defaultValue = "") String name
    ) {
        var startup = userService.createOrganization(authentication.getName(), name);
        return ok(getResponse(request, Map.of("organization", startup), "Organization created successfully", CREATED));
    }

    @GetMapping("/organization")
    public ResponseEntity<Response> getStartup(
            Authentication authentication,
            HttpServletRequest request
    ) {
        var startup = userService.getStartup(authentication.getName());
        return ok(getResponse(request,Map.of("organization", startup),"startup fetched successfully", OK)
        );
    }

    @PostMapping("/invite")
    public ResponseEntity<Response> inviteUser(
            Authentication authentication,
            HttpServletRequest request,
            @RequestParam String startupUuid,
            @RequestParam String email,
            @RequestParam String role
    ) {
        var invitation = userService.inviteUser(authentication.getName(), startupUuid, email, role);
        return ok(getResponse(request, Map.of("invitation", invitation), "invitation sent", CREATED));
    }

    @GetMapping("/invitations")
    public ResponseEntity<Response> getMyInvitations(
            Authentication authentication,
            HttpServletRequest request
    ) {
        var invitations = userService.getMyInvitations(authentication.getName());
        return ok(getResponse(request, Map.of("invitations", invitations),"success",OK));
    }

    @PostMapping("/invite/accept/{invitationUuid}")
    public ResponseEntity<Response> acceptInvitation(
            Authentication authentication,
            HttpServletRequest request,
            @PathVariable("invitationUuid") String invitationUuid
    ) {
        var result = userService.acceptInvitation(invitationUuid ,authentication.getName() );
        return ok(getResponse(request,Map.of("membership", result),"invitation accepted",OK));
    }
    @PatchMapping("/mfa/enable")
    public ResponseEntity<Response> enableMfa(@NotNull Authentication authentication, HttpServletRequest request) {
        var user = userService.enableMfa(authentication.getName());
        return ok(getResponse(request , Map.of("user" ,user) , "2FA enabled successfully", OK));
    }

    @PatchMapping("/mfa/disable")
    public ResponseEntity<Response> disableMfa(@NotNull Authentication authentication, HttpServletRequest request) {
        var user = userService.disableMfa(authentication.getName());
        return ok(getResponse(request , Map.of("user" ,user) , "2FA disabled successfully", OK));
    }

    @GetMapping("/profile")
    public ResponseEntity<Response> profile(@NotNull Authentication authentication, HttpServletRequest request) {
        var user = userService.getUserByUuid(authentication.getName());
        var devices = userService.geDevices(authentication.getName());
        return ok(getResponse(request , Map.of("user" ,user , "devices" , devices) , "Profile retrieved", OK));
    }

    @GetMapping("/{userUuid}")
    public ResponseEntity<Response> getUserByUuid(@NotNull Authentication authentication, @PathVariable("userUuid") String uuid , HttpServletRequest request) {
        var user = userService.getUserByUuid(authentication.getName());
        return ok(getResponse(request , Map.of("user" ,user) , "Profile retrieved", OK));
    }

    @GetMapping("/assignee/{ticketUuid}")
    public ResponseEntity<Response> getAssigneeByUuid(@NotNull Authentication authentication, @PathVariable("ticketUuid") String ticketUuid , HttpServletRequest request) {
        var user = userService.getAssignee(ticketUuid);
        return ok(getResponse(request , Map.of("user" ,user ) , "Profile retrieved", OK));
    }

    @GetMapping("/techsupports")
    public ResponseEntity<Response> getTechSupports(@NotNull Authentication authentication , HttpServletRequest request) {
        var techSupports = userService.getTechSupports();
        return ok(getResponse(request , Map.of("techSupports" ,techSupports ) , "Profile retrieved", OK));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<Response> getUserByEmail(@NotNull Authentication authentication, @PathVariable("email") String email , HttpServletRequest request) {
        var user = userService.getUserByEmail(email);
        return ok(getResponse(request , Map.of("user" ,user ) , "Profile retrieved", OK));
    }

    @GetMapping("/credential/{userUuid}")
    public ResponseEntity<Response> getCredential(@NotNull Authentication authentication, @PathVariable("userUuid") String userUuid , HttpServletRequest request) {
        var credential = userService.getCredential(userUuid);
        return ok(getResponse(request , Map.of("credential" ,credential ) , "Profile retrieved", OK));
    }

    @PatchMapping("/update")
    public ResponseEntity<Response> updateUser(@NotNull Authentication authentication, @RequestBody UserRequest user, HttpServletRequest request) {
        var updatedUser = userService.updateUser(authentication.getName() , user.getFirstName() , user.getLastName() , user.getEmail(), user.getPhone() , user.getBio() , user.getAddress());
        return ok(getResponse(request , Map.of("user" ,updatedUser ) , "User updated Successfully", OK));
    }

    @PatchMapping("/updaterole")
    public ResponseEntity<Response> updateRole(@NotNull Authentication authentication, @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        var updateUser = userService.updateRole(authentication.getName() , roleRequest.getRole());
        return ok(getResponse(request , Map.of("user" ,updateUser ) , "User updated Successfully", OK));
    }

    @PatchMapping("/toggleaccountexpired")
    public ResponseEntity<Response> toggleAccountExpired(@NotNull Authentication authentication, HttpServletRequest request) {
        var user = userService.toggleAccountExpired(authentication.getName());
        return ok(getResponse(request , Map.of("user" ,user ) , "User updated Successfully", OK));
    }

    @PatchMapping("/toggleaccountlocked")
    public ResponseEntity<Response> toggleAccountLocked(@NotNull Authentication authentication, HttpServletRequest request) {
        var user = userService.toggleAccountLocked(authentication.getName());
        return ok(getResponse(request , Map.of("user" ,user ) , "User updated Successfully", OK));
    }

    @PatchMapping("/toggleaccountenabled")
    public ResponseEntity<Response> toggleAccountEnabled(@NotNull Authentication authentication, HttpServletRequest request) {
        var user = userService.toggleAccountEnabled(authentication.getName());
        return ok(getResponse(request , Map.of("user" ,user ) , "User updated Successfully", OK));
    }

    @PatchMapping("/updatepassword")
    public ResponseEntity<Response> updatePassword(@NotNull Authentication authentication, @RequestBody PasswordRequest PasswordRequest , HttpServletRequest request) {
         userService.updatePassword(authentication.getName(), PasswordRequest.getCurrentPassword() , PasswordRequest.getNewPassword() , PasswordRequest.getConfirmNewPassword());
        return ok(getResponse(request , emptyMap() , "password updated Successfully", OK));
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<Response> resetPassword(@RequestParam("email") String email , HttpServletRequest request) {
        userService.resetPassword(email);
        return ok(getResponse(request , emptyMap() , "We sent you an email to reset your password", OK));
    }

    @GetMapping("/verify/password")
    public ResponseEntity<Response> verifyPassword(@RequestParam("token") String token , HttpServletRequest request) {
      var user =  userService.verifyPasswordToken(token);
        return ok(getResponse(request , Map.of("user" , user) , "We sent you an email to reset your password", OK));
    }

    @PostMapping("/resetpassword/reset")
    public ResponseEntity<Response> doResetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest , HttpServletRequest request) {
        userService.doResetPassword(resetPasswordRequest.getUserUuid() , resetPasswordRequest.getToken() , resetPasswordRequest.getPassword() , resetPasswordRequest.getConfirmPassword());
        return ok(getResponse(request , emptyMap() , "Password reset successfully you may login in now", OK));
    }

    @GetMapping("/list")
    public ResponseEntity<Response> getUsers(@NotNull Authentication authentication, HttpServletRequest request) {
        return ok(getResponse(request , Map.of("users" , userService.getUsers()) , "password updated Successfully", OK));
    }

    @PatchMapping("/photo")
    public ResponseEntity<Response> uploadPhoto(@NotNull Authentication authentication, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        var user = userService.uploadPhoto(authentication.getName() , file);
        return ok(getResponse(request , Map.of("user" , user) , "photo updated Successfully", OK));
    }

    @GetMapping(path = "/photo/{filename}" , produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public byte [] getPhoto(@PathVariable("filename") MultipartFile fileName) throws IOException {
            return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + fileName));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchUsers(@RequestParam String q ,HttpServletRequest request) {
        return ok(getResponse(request, Map.of("users", userService.searchUsers(q)), "users retrieved successfully", OK)
        );
    }

        private URI getUri(){
            return URI.create("/user/profile/u" );
        }

}
