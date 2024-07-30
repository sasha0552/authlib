package com.mojang.authlib.yggdrasil.response;

public class PrivilegesResponse extends Response {
/*
{
    "privileges": {
        "onlineChat": {
            "enabled": true
        },
        "multiplayerServer": {
            "enabled": true
        },
        "multiplayerRealms": {
            "enabled": true
        }
    }
}
*/
    private Privileges privileges = new Privileges();

    public Privileges getPrivileges() {
        return privileges;
    }

    public class Privileges {
        private Privilege onlineChat = new Privilege();
        private Privilege multiplayerServer = new Privilege();
        private Privilege multiplayerRealms = new Privilege();

        public Privilege getOnlineChat() {
            return onlineChat;
        }

        public Privilege getMultiplayerServer() {
            return multiplayerServer;
        }

        public Privilege getMultiplayerRealms() {
            return multiplayerRealms;
        }

        public class Privilege {
            private boolean enabled;

            public boolean isEnabled() {
                return enabled;
            }
        }
    }
}
