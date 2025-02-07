package com.example.demo.Cont;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.CreateRoom;
import com.example.demo.Model.EPpojo;
import com.example.demo.Model.data1;
import com.example.demo.Model.pojo;
import com.example.demo.Repo.ConnectivityDB;
import com.example.demo.Repo.YoutubeUrl;
import com.example.demo.Repo.createRoomConnect;

import io.github.bonigarcia.wdm.WebDriverManager;



@RestController
@CrossOrigin(origins = "https://grp-sync-front1.vercel.app")
public class RestCont {
    
    @Autowired
    ConnectivityDB d1;

    

    @Autowired
    data1 d;

 

    @PostMapping("register")
    @ResponseBody
    public String Register(@RequestBody EPpojo d) {
        pojo p = new pojo();
        if (d1.findByUsername(d.getEmail()) != null) {
            return "Email already registered";
        }
       
        p.setUsername(d.getEmail());
        p.setPassword(d.getPassword());
        p.setRole("User");
        d1.save(p);
        return "Registered Sucessfully";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody Map<String, String> param) { 
         pojo p = d1.findByUsername(param.get("username"));
       
            if (p == null) {
                return "User not found";
            }
            if (p.getPassword().equals(param.get("password"))) {
                return "Login Sucessful";
            }
            return "Password Incorrect";
      
    }

   

    @PostMapping("/getdata")
    @ResponseBody
    public data1 Webcrawl(@RequestBody YoutubeUrl youtubeUrl) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-software-rasterizer");
        options.setBinary("/usr/bin/chromium"); // Set Chrome binary path
        options.addArguments("--remote-debugging-port=9222");
        
        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            String videoUrl = youtubeUrl.getYoutubeUrl();
            driver.get(videoUrl);
            
            // Increased wait time
            Thread.sleep(5000);
            
            // Get the page title
            String videoTitle = driver.getTitle();
            
            // More robust channel name extraction
            String channelName;
            try {
                WebElement channelElement = driver.findElement(By.cssSelector("#owner #channel-name"));
                channelName = channelElement.getText().trim();
            } catch (Exception e) {
                channelName = "Channel name not found";
            }
            
            d.setTitle(videoTitle);
            d.setUrl(videoUrl);
            d.setName(channelName);
            
            return d;
            
        } catch (Exception e) {
            System.err.println("Error during web scraping: " + e.getMessage());
            e.printStackTrace();
            d.setUrl(youtubeUrl.getYoutubeUrl());
            d.setTitle("Error fetching title");
            d.setName("Error fetching name");
            return d;
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

   @Autowired
   createRoomConnect crc;
    @PostMapping("/getRoomData")
    @ResponseBody
    public String getMethodName(@RequestBody Map<String, String> param) {
        if(crc.findByRoomName(param.get("roomName")) != null) {
            return "Room Already Exists";
        }
        CreateRoom cr = new CreateRoom();
        String roomName = param.get("roomName");
        String roomPassword = param.get("roomPassword");
        String roomAdmin = param.get("username");
        cr.setRoomName(roomName);
        cr.setJoinRoomPassword(roomPassword);
        cr.setRoomAdmin(roomAdmin);
        crc.save(cr);
        return "Room Created";
    }

    @PostMapping("/check")
    @ResponseBody
    public String check(@RequestBody Map<String, String> param) {
        CreateRoom cr = crc.findByRoomName(param.get("roomName"));
        if (cr == null) {
            return "Room Not Found";
        }
        if (cr.getJoinRoomPassword() != null && cr.getJoinRoomPassword().equals(param.get("joinRoomPassword"))) {
            return "Room Found";
        }
        return "Room Password Incorrect";
    }
  
    @MessageMapping("/room/{roomName}")
    @SendTo("/topic/room/{roomName}")
    public Integer getRoomdetails(@RequestBody Map<String, String> param) {
        String roomName = param.get("roomName");

        
       
      
        return 1;
    }
    @MessageMapping("/chat1/{roomName}")
    @SendTo("/topic/chat1/{roomName}")
    @ResponseBody
    public Map<String , String> getRoomChat(@RequestBody Map<String, String> param) {
      
        return param;
    }

    @MessageMapping("/playlist/{roomName}")
    @SendTo("/topic/playlist/{roomName}")
    
    public Map<String , ArrayList> getPlaylist(@RequestBody Map<String, ArrayList> param) {
        return param;
    }

    @MessageMapping("disconnect")
    @SendTo("/topic/disconnect")
    public void disconnect(@RequestBody Map<String, String> param) {
       
        if(crc.findByRoomAdmin(param.get("username")) != null) {
           crc.delete(crc.findByRoomAdmin(param.get("username")));
              
       }      
      
    }
    
    @MessageMapping("/song/{roomName}")
    @SendTo("/topic/song/{roomName}")
    public Map<String , String> getsong(@RequestBody Map<String, String> param) {
        return param;
    }

 
   
  

}
