

# user-center2的前后端开发说明

在这之前，可以开发一个/home做主页，但能跳转到user/login，或者直接user/login做主页。

- 登录

```java
/**
     * 用户登录,返回用户信息，并将用户信息存入session
     * @param userLoginRequest
     * @param request
     * @return
     */
@RequestMapping("/login")
public User login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request)
```

@RequestBody能够将一个JSON格式的对象反序列化成我这边想接收的对象，然后我再从这个后端存在的对象里去取字段，所以前端的登录要给我返回一个对应的JSON对象**（应该有id，password字段）**。

前端应该能收到返回的用户信息（可做处理如回显xxx登录成功之类的），并且浏览器的会话也能拿到该信息（这是长久的，后续做role验证都要用）。

这时前端可以通过拿到的用户角色信息做判断，可以重定向到不同的角色初始页面，比如从localhost:8080/user/login->localhost:8080/home/admin。你们可以再这个角色初始页面上设计内容，然后再跳转到user/xxx的功能页面，记得留一个返回首页的按键。

- 退出

```Java
@RequestMapping("/logout")
public void logout(HttpServletRequest request) {
    request.getSession().removeAttribute("USER_LOGIN_STATE");
}
```

用户退出登录并从会话中清除信息，我希望前端还能做一个重定向回首页

## 管理员阶段

- 添加用户

  ```java
  /**
   * 管理员根据姓名，角色增加用户
   * @param user
   * @return
   */
  @GetMapping("/addUser")
  public boolean addUser(@RequestBody User user,HttpServletRequest request)
  ```

通过会话拿到登录的用户信息，做角色身份校验，若是管理员即可添加其他用户

前端应该给后端传入一个json格式对象**（应该包含name和role字段）**，后端反序列化成一个user对象，应该包含name和role字段。

- 添加课程（注意我这边添加的课程没有对应的选课，管理员后续还应该添加课程表信息）

  ```Java
  /**
       * 管理员添加课程，若课程已存在会报错
       *如果成功返回true
       */
  @GetMapping("/addCourse")
  public boolean addCourse(@RequestBody Course course, HttpServletRequest request)
  ```

前端应该传来course_name的json格式

- 添加课程表

  ```Java
  @GetMapping("/addCourseSchedule")
  public boolean addCourseSchedule(@RequestBody CourseSchedule courseSchedule, HttpServletRequest request)
  ```

前端应该返回以下所有字段**course_id**和**teacher_id**和**semester**和**time**和**capacity**的json字段

如果有重复课程或者有字段为空返回false，否则返回true添加成功

- 查询用户

  ```Java
  /**
   * 管理员查询用户,返回的是一个用户列表（同名同姓）
   * @param user
   * @return
   */
  @GetMapping("/selectUser")
  public List<User> selectUser(@RequestBody User user, HttpServletRequest request)
  ```

前端应该传给一个json格式的**name**字段

后端返回一个user列表

- 查询课程

  ```Java
  @GetMapping("/selectCourse")
  public Course selectCourse(@RequestBody Course course, HttpServletRequest request) 
  ```

只要是用户就都能查询课程

前端应该返回一个**course_id**和**course_name**的json字段（两者返回一个或者都返回）

后端返回Coure对象

- 查询课程表

  ```Java
  @GetMapping("/selectCourseSchedule")
  public List<CourseSchedule> selectCourseSchedule(@RequestBody CourseSchedule courseSchedule,
                                                   HttpServletRequest httpServletRequest)
  ```

只要是用户就都能查询课程表

前端应该至少返回一个**course_id**或**teacher_id**或**semester**或**time**或**capacity**的json字段（至少要有course_id）

但前端其实只留下前四个字段选项就够了，这里capacity可以永远不传

后端返回**CoureSchedule**列表

**====================注意我把course_schedule的time并入了主键，对数据库的修改====================**

- 学生选课

  ```java
  @GetMapping("/courseSelect")
  public boolean courseSelect(@RequestBody UserSelectCourseScheduleRequest courseSchedule,
                              HttpServletRequest httpServletRequest)
  ```

如果是管理员需要多传一个student_id字段，学生就可以让student_id为null，成功选课返回true

模板格式

- 管理员版

```json
{
  "course_id": 1016,
  "semester": 202304,
  "student_id": 21109,
  "teacher_id": 10030,
  "time": "二1-2",
  "capacity": null
}
```

- 学生版

```json
GET http://localhost:8080/user/courseSelect
Content-Type: application/json

{
  "course_id": 1016,
  "semester": 202304,
  "student_id": null,
  "teacher_id": 10030,
  "time": "二1-2",
  "capacity": null
}
```
