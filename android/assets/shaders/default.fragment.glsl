#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;

void main()
{
  vec4 result = vec4(0);
  result.a = length(v_texCoords);
  result = texture2D(u_texture, vec2(atan(v_texCoords.y, v_texCoords.x), .2/result.a)+u_time)*result.a;
  gl_FragColor = result;
}