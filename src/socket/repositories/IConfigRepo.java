package socket.repositories;

import socket.config.Config;

public interface IConfigRepo {
    Config getConfigData();
    void setConfigData(Config config);
}
