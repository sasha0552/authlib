package com.mojang.authlib.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;

public interface MinecraftSessionService {
    /**
     * Attempts to join the specified Minecraft server.
     * <p />
     * The {@link com.mojang.authlib.GameProfile} used to join with may be partial, but the exact requirements will vary on
     * authentication service. If this method returns without throwing an exception, the join was successful and a subsequent call to
     * {@link #hasJoinedServer(com.mojang.authlib.GameProfile, String)} will return true.
     *
     * @param profile Partial {@link com.mojang.authlib.GameProfile} to join as
     * @param authenticationToken The {@link com.mojang.authlib.UserAuthentication#getAuthenticatedToken() authenticated token} of the user
     * @param serverId The random ID of the server to join
     * @throws com.mojang.authlib.exceptions.AuthenticationUnavailableException Thrown when the servers return a malformed response, or are otherwise unavailable
     * @throws com.mojang.authlib.exceptions.InvalidCredentialsException Thrown when the specified authenticationToken is invalid
     * @throws com.mojang.authlib.exceptions.AuthenticationException Generic exception indicating that we could not authenticate the user
     */
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException;

    /**
     * Checks if the specified user has joined a Minecraft server.
     * <p />
     * The {@link com.mojang.authlib.GameProfile} used to join with may be partial, but the exact requirements will vary on
     * authentication service.
     *
     * @param user Partial {@link com.mojang.authlib.GameProfile} to check for
     * @param serverId The random ID of the server to check for
     * @throws com.mojang.authlib.exceptions.AuthenticationUnavailableException Thrown when the servers return a malformed response, or are otherwise unavailable
     * @return Full game profile if the user had joined, otherwise null
     */
    public GameProfile hasJoinedServer(GameProfile user, String serverId) throws AuthenticationUnavailableException;
}
